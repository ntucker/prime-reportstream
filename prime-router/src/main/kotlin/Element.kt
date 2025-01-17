package gov.cdc.prime.router

import com.google.i18n.phonenumbers.PhoneNumberUtil
import gov.cdc.prime.router.Element.Cardinality.ONE
import gov.cdc.prime.router.Element.Cardinality.ZERO_OR_ONE
import gov.cdc.prime.router.common.Environment
import gov.cdc.prime.router.metadata.ElementAndValue
import gov.cdc.prime.router.metadata.LIVDLookupMapper
import gov.cdc.prime.router.metadata.LookupMapper
import gov.cdc.prime.router.metadata.LookupTable
import gov.cdc.prime.router.metadata.Mapper
import java.lang.Exception
import java.text.DecimalFormat
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAccessor
import java.util.Locale

class AltValueNotDefinedException(message: String) : IllegalStateException(message)

/**
 * An element is represents a data element (ie. a single logical value) that is contained in single row
 * of a report. A set of Elements form the main content of a Schema.
 *
 * In some sense the element is like the data element in other data schemas that engineers are familiar with.
 * For the data-hub, the data element contains information specific to public health. For a given topic,
 * there is a "standard" schema with elements. The logically the mapping process is:
 *
 *    Schema 1 -> Standard Standard -> schema 2
 *
 * To describe the intent of an element there are references to the national standards.
 */
data class Element(
    // An element can either be a new element or one based on previously defined element
    // - A name of form [A-Za-z0-9_]+ is a new element
    // - A name of form [A-Za-z0-9_]+.[A-Za-z0-9_]+ is an element based on a previously defined element
    //
    val name: String,

    /**
     * Type of the element
     */
    val type: Type? = null,

    // Either valueSet or altValues must be defined for a CODE type
    val valueSet: String? = null,
    val valueSetRef: ValueSet? = null, // set during fixup
    val altValues: List<ValueSet.Value>? = null,

    // table and tableColumn must be defined for a TABLE type
    val table: String? = null,
    val tableRef: LookupTable? = null, // set during fixup
    val tableColumn: String? = null, // set during fixup

    val cardinality: Cardinality? = null,
    val pii: Boolean? = null,
    val phi: Boolean? = null,
    val maxLength: Int? = null, // used to truncate outgoing formatted String fields.  null == no length limit.
    val default: String? = null,
    val defaultOverridesValue: Boolean? = null,
    val mapper: String? = null,
    val mapperOverridesValue: Boolean? = null,
    val mapperRef: Mapper? = null, // set during fixup
    val mapperArgs: List<String>? = null, // set during fixup

    // Information about the elements definition.
    val reference: String? = null,
    val referenceUrl: String? = null,
    val hhsGuidanceField: String? = null,
    val natFlatFileField: String? = null,

    // Format specific information used to format output

    // HL7 specific information
    val hl7Field: String? = null,
    val hl7OutputFields: List<String>? = null,
    val hl7AOEQuestion: String? = null,

    /**
     * The header fields that correspond to an element.
     * A element can output to multiple CSV fields.
     * The first field is considered the primary field. It is used
     * on input define the element
     */
    val csvFields: List<CsvField>? = null,

    // FHIR specific information
    val fhirField: String? = null,

    // a field to let us incorporate documentation data (markdown)
    // in the schema files so we can generate documentation off of
    // the file
    val documentation: String? = null,

    // used for the concatenation mapper. the element carries this
    // value around and into the mapper itself so the interface for the
    // mapper remains as generic as possible
    val delimiter: String? = null,

    // used to be able to send blank values for fields that get validated/normalized
    // in serializers.
    // for instance, a badly formatted yet optional date field.
    val nullifyValue: Boolean = false
) {
    /**
     * Types of elements. Types imply a specific format and fake generator.
     */
    enum class Type {
        TEXT,
        TEXT_OR_BLANK, // Blank values are valid (not null)
        NUMBER,
        DATE,
        DATETIME,
        DURATION,
        CODE, // CODED with a HL7, SNOMED-CT, LONIC valueSet
        TABLE, // A table column value
        TABLE_OR_BLANK,
        EI, // A HL7 Entity Identifier (4 parts)
        HD, // ISO Hierarchic Designator
        ID, // Generic ID
        ID_CLIA, // CMS CLIA number (must follow CLIA format rules)
        ID_DLN,
        ID_SSN,
        ID_NPI,
        STREET,
        STREET_OR_BLANK,
        CITY,
        POSTAL_CODE,
        PERSON_NAME,
        TELEPHONE,
        EMAIL,
        BLANK,
    }

    data class CsvField(
        val name: String,
        val format: String?,
    )

    data class HDFields(
        val name: String,
        val universalId: String?,
        val universalIdSystem: String?
    )

    data class EIFields(
        val name: String,
        val namespace: String?,
        val universalId: String?,
        val universalIdSystem: String?
    )

    /**
     * An element can have subfields, for example when more than CSV field makes up a single element.
     * See ElementTests for an example.
     **/
    data class SubValue(
        val name: String,
        val value: String,
        val format: String?
    )

    /**
     * @property ZERO_OR_ONE Can be null or present (default)
     * @property ONE Must be present, error if not present
     */
    enum class Cardinality {
        ZERO_OR_ONE,
        ONE;
        // ZERO is not a value, just remove the element to represent this concept
        // Other values including conditionals in the future.

        fun toFormatted(): String {
            return when (this) {
                ZERO_OR_ONE -> "[0..1]"
                ONE -> "[1..1]"
            }
        }
    }

    val isCodeType get() = this.type == Type.CODE

    val isOptional
        get() = this.cardinality == null ||
            this.cardinality == ZERO_OR_ONE || canBeBlank

    val canBeBlank
        get() = type == Type.TEXT_OR_BLANK ||
            type == Type.STREET_OR_BLANK ||
            type == Type.TABLE_OR_BLANK ||
            type == Type.BLANK

    /**
     * True if this element has a table lookup.
     */
    val isTableLookup get() = mapperRef != null && type == Type.TABLE

    /**
     * String showing the external field name(s) if any and the element name.
     */
    val fieldMapping: String get() {
        return when {
            !csvFields.isNullOrEmpty() -> "${csvFields.map { it.name }.joinToString(",")} ($name)"
            !hl7Field.isNullOrBlank() -> "$hl7Field ($name)"
            !hl7OutputFields.isNullOrEmpty() -> "${hl7OutputFields.joinToString(",")}} ($name)"
            else -> "($name)"
        }
    }

    fun inheritFrom(baseElement: Element): Element {
        return Element(
            name = this.name,
            type = this.type ?: baseElement.type,
            valueSet = this.valueSet ?: baseElement.valueSet,
            valueSetRef = this.valueSetRef ?: baseElement.valueSetRef,
            altValues = this.altValues ?: baseElement.altValues,
            table = this.table ?: baseElement.table,
            tableColumn = this.tableColumn ?: baseElement.tableColumn,
            cardinality = this.cardinality ?: baseElement.cardinality,
            pii = this.pii ?: baseElement.pii,
            phi = this.phi ?: baseElement.phi,
            maxLength = this.maxLength ?: baseElement.maxLength,
            mapper = this.mapper ?: baseElement.mapper,
            mapperOverridesValue = this.mapperOverridesValue ?: baseElement.mapperOverridesValue,
            default = this.default ?: baseElement.default,
            defaultOverridesValue = this.defaultOverridesValue ?: baseElement.defaultOverridesValue,
            reference = this.reference ?: baseElement.reference,
            referenceUrl = this.referenceUrl ?: baseElement.referenceUrl,
            hhsGuidanceField = this.hhsGuidanceField ?: baseElement.hhsGuidanceField,
            natFlatFileField = this.natFlatFileField ?: baseElement.natFlatFileField,
            hl7Field = this.hl7Field ?: baseElement.hl7Field,
            hl7OutputFields = this.hl7OutputFields ?: baseElement.hl7OutputFields,
            hl7AOEQuestion = this.hl7AOEQuestion ?: baseElement.hl7AOEQuestion,
            documentation = this.documentation ?: baseElement.documentation,
            csvFields = this.csvFields ?: baseElement.csvFields,
            delimiter = this.delimiter ?: baseElement.delimiter,
        )
    }

    /**
     * Generate validation error messages if this element is not valid.
     * @return a list of error messages, or an empty list if no errors
     */
    fun validate(): List<String> {
        val errorList = mutableListOf<String>()

        /**
         * Add an error [message].
         */
        fun addError(message: String) {
            errorList.add("Element $name - $message.")
        }

        // All elements require a type
        if (type == null) addError("requires an element type.")

        // Table lookups require a table
        if ((mapperRef?.name == LookupMapper().name || mapperRef?.name == LIVDLookupMapper().name) &&
            (tableRef == null || tableColumn.isNullOrBlank())
        )
            addError("requires a table and table column.")

        // Elements of type table need a table ref
        if ((type == Type.TABLE || type == Type.TABLE_OR_BLANK || !tableColumn.isNullOrBlank()) && tableRef == null)
            addError("requires a table.")

        // Elements with mapper parameters require a mapper
        if ((mapperOverridesValue == true || !mapperArgs.isNullOrEmpty()) && mapperRef == null)
            addError("has mapper related parameters, but no mapper.")

        // Elements that can be blank should not have a default
        if (canBeBlank && default != null)
            addError("has a default specified, but can be blank")

        return errorList
    }

    fun nameContains(substring: String): Boolean {
        return name.contains(substring, ignoreCase = true)
    }

    /**
     * Is there a default value for this element?
     *
     * @param defaultValues a dynamic set of default values to use
     */
    fun hasDefaultValue(defaultValues: DefaultValues): Boolean {
        return defaultValues.containsKey(name) || default?.isNotBlank() == true
    }

    fun defaultValue(defaultValues: DefaultValues): String {
        return defaultValues.getOrDefault(name, default ?: "")
    }

    /**
     * A formatted string is the Element's normalized value formatted using the format string passed in
     * The format string's value is specific to the type of the element.
     */
    fun toFormatted(
        normalizedValue: String,
        format: String? = null,
    ): String {
        val cleanedNormalizedValue = normalizedValue.trim()
        if (cleanedNormalizedValue.isEmpty()) return ""
        val formattedValue = when (type) {
            // sometimes you just need to send through an empty column
            Type.BLANK -> ""
            Type.DATE -> {
                if (format != null) {
                    val ta = parseDate(cleanedNormalizedValue)
                    getDate(ta, format)
                } else {
                    cleanedNormalizedValue
                }
            }
            Type.DATETIME -> {
                if (format != null) {
                    val formatter = DateTimeFormatter.ofPattern(format)
                    LocalDateTime.parse(cleanedNormalizedValue, datetimeFormatter).format(formatter)
                } else {
                    cleanedNormalizedValue
                }
            }
            Type.CODE -> {
                // First, prioritize use of a local $alt format, even if no value set exists.
                when (format) {
                    // TODO Revisit: there may be times that normalizedValue is not an altValue
                    altDisplayToken ->
                        toAltDisplay(cleanedNormalizedValue)
                            ?: throw AltValueNotDefinedException(
                                "Outgoing receiver schema problem:" +
                                    " '$cleanedNormalizedValue' is not in altValues set for $fieldMapping."
                            )
                    codeToken ->
                        toCode(cleanedNormalizedValue)
                            ?: error(
                                "Schema Error: " +
                                    "'$cleanedNormalizedValue' is not in valueSet " +
                                    "'$valueSet' for $fieldMapping/'$format'. " +
                                    "\nAvailable values are " +
                                    "${valueSetRef?.values?.joinToString { "${it.code} -> ${it.display}" }}" +
                                    "\nAlt values (${altValues?.count()}) are " +
                                    "${altValues?.joinToString { "${it.code} -> ${it.display}" }}"
                            )
                    caretToken -> {
                        val display = valueSetRef?.toDisplayFromCode(cleanedNormalizedValue)
                            ?: error("Internal Error: '$cleanedNormalizedValue' cannot be formatted for $fieldMapping")
                        "$cleanedNormalizedValue^$display^${valueSetRef.systemCode}"
                    }
                    displayToken -> {
                        valueSetRef?.toDisplayFromCode(cleanedNormalizedValue)
                            ?: error("Internal Error: '$cleanedNormalizedValue' cannot be formatted for $fieldMapping")
                    }
                    systemToken -> {
                        // Very confusing, but this special case is in the HHS Guidance Confluence page
                        if (valueSetRef?.name == "hl70136" && cleanedNormalizedValue == "UNK")
                            "NULLFL"
                        else
                            valueSetRef?.systemCode ?: error("valueSetRef for $valueSet is null!")
                    }
                    else -> cleanedNormalizedValue
                }
            }
            Type.TELEPHONE -> {
                // normalized telephone always has 3 values national:country:extension
                val parts = if (cleanedNormalizedValue.contains(phoneDelimiter)) {
                    cleanedNormalizedValue.split(phoneDelimiter)
                } else {
                    // remove parens from HL7 formatting
                    listOf(
                        cleanedNormalizedValue
                            .replace("(", "")
                            .replace(")", ""),
                        "1", // country code
                        "" // extension
                    )
                }

                (format ?: defaultPhoneFormat)
                    .replace(countryCodeToken, parts[1])
                    .replace(areaCodeToken, parts[0].substring(0, 3))
                    .replace(exchangeToken, parts[0].substring(3, 6))
                    .replace(subscriberToken, parts[0].substring(6))
                    .replace(extensionToken, parts[2])
                    .replace(e164Token, "+${parts[1]}${parts[0]}")
            }
            Type.POSTAL_CODE -> {
                when (format) {
                    zipFiveToken -> {
                        // If this is US zip, return the first 5 digits
                        val matchResult = Regex(usZipFormat).matchEntire(cleanedNormalizedValue)
                        matchResult?.groupValues?.get(1)
                            ?: cleanedNormalizedValue.padStart(5, '0')
                    }
                    zipFivePlusFourToken -> {
                        // If this a US zip, either 5 or 9 digits depending on the value
                        val matchResult = Regex(usZipFormat).matchEntire(cleanedNormalizedValue)
                        if (matchResult != null && matchResult.groups[2] == null) {
                            matchResult.groups[1]?.value ?: ""
                        } else if (matchResult != null && matchResult.groups[2] != null) {
                            "${matchResult.groups[1]?.value}-${matchResult.groups[2]?.value}"
                        } else {
                            cleanedNormalizedValue.padStart(5, '0')
                        }
                    }
                    else -> cleanedNormalizedValue.padStart(5, '0')
                }
            }
            Type.HD -> {
                val hdFields = parseHD(cleanedNormalizedValue)
                when (format) {
                    null,
                    hdNameToken -> hdFields.name
                    hdUniversalIdToken -> hdFields.universalId ?: ""
                    hdSystemToken -> hdFields.universalIdSystem ?: ""
                    else -> error("Schema Error: unsupported HD format for output: '$format' in $fieldMapping")
                }
            }
            Type.EI -> {
                val eiFields = parseEI(cleanedNormalizedValue)
                when (format) {
                    null,
                    eiNameToken -> eiFields.name
                    eiNamespaceIdToken -> eiFields.namespace ?: ""
                    eiUniversalIdToken -> eiFields.universalId ?: ""
                    eiSystemToken -> eiFields.universalIdSystem ?: ""
                    else -> error("Schema Error: unsupported EI format for output: '$format' in $fieldMapping")
                }
            }
            else -> cleanedNormalizedValue
        }
        return truncateIfNeeded(formattedValue)
    }

    // this method takes a date value as a string and returns a
    // TemporalAccessor based on the variable date time pattern
    fun parseDate(dateValue: String): TemporalAccessor {
        return DateTimeFormatter.ofPattern(variableDateTimePattern)
            .parseBest(dateValue, OffsetDateTime::from, LocalDateTime::from, Instant::from, LocalDate::from)
    }

    // given a temporal accessor this will check the type that it needs to return
    // and then output based on the format. you can extend this to accept a third
    // variable which would be the element's output format, and do an extra branch
    // based on that
    fun getDate(
        temporalAccessor: TemporalAccessor,
        outputFormat: String,
        convertPositiveOffsetToNegative: Boolean = false
    ): String {
        val outputFormatter = DateTimeFormatter.ofPattern(outputFormat)
        val formattedDate = when (temporalAccessor) {
            is LocalDate -> LocalDate.from(temporalAccessor)
                .atStartOfDay()
                .format(outputFormatter)
            is LocalDateTime -> LocalDateTime.from(temporalAccessor)
                .format(outputFormatter)
            is OffsetDateTime -> OffsetDateTime.from(temporalAccessor)
                .format(outputFormatter)
            is Instant -> Instant.from(temporalAccessor).toString()
            else -> error("Unsupported format!")
        }

        return if (convertPositiveOffsetToNegative) {
            convertPositiveOffsetToNegativeOffset(formattedDate)
        } else {
            formattedDate
        }
    }

    fun truncateIfNeeded(str: String): String {
        if (maxLength == null) return str // no maxLength is very common
        if (str.isEmpty()) return str
        // Only TEXTy fields can be truncated, and only if a valid maxLength is set in the schema
        return when (type) {
            Type.TEXT,
            Type.TEXT_OR_BLANK,
            Type.STREET,
            Type.STREET_OR_BLANK,
            Type.CITY,
            Type.PERSON_NAME,
            Type.EMAIL -> {
                if (str.length <= maxLength)
                    str
                else
                    str.substring(0, maxLength)
            }
            else -> str
        }
    }

    /**
     * Take a formatted value and check to see if it can be stored in a report.
     */
    fun checkForError(formattedValue: String, format: String? = null): ActionLogDetail? {
        // remove trailing spaces
        val cleanedValue = formattedValue.trim()
        if (cleanedValue.isBlank() && !isOptional && !canBeBlank) return MissingFieldMessage(fieldMapping)
        return when (type) {
            Type.DATE -> {
                try {
                    LocalDate.parse(cleanedValue)
                    return null
                } catch (e: DateTimeParseException) {
                    // continue to the next try
                }
                try {
                    val formatter = DateTimeFormatter.ofPattern(format ?: datePattern, Locale.ENGLISH)
                    LocalDate.parse(cleanedValue, formatter)
                    return null
                } catch (e: DateTimeParseException) {
                    // continue to the next try
                }

                // the next six date validation patterns are valid date patterns that we have seen be
                // manually entered into EMR systems, but are not consistent, so we cannot use the "format" param
                try {
                    validateManualDates(cleanedValue, true)
                    return null
                } catch (e: DateTimeParseException) {
                    // continue to the next try
                }
                try {
                    val optionalDateTime = variableDateTimePattern
                    val df = DateTimeFormatter.ofPattern(optionalDateTime)
                    val ta = df.parseBest(
                        cleanedValue,
                        OffsetDateTime::from,
                        LocalDateTime::from,
                        Instant::from,
                        LocalDate::from
                    )
                    LocalDate.from(ta)
                    return null
                } catch (e: DateTimeParseException) {
                    if (nullifyValue) {
                        return null
                    } else {
                        InvalidDateMessage(cleanedValue, fieldMapping, format)
                    }
                }
            }
            Type.DATETIME -> {
                try {
                    // getDateTime will throw exception it there is any error.
                    getDateTime(cleanedValue, format)
                    return null
                } catch (e: DateTimeParseException) {
                    // continue to the next try
                } catch (e: DateTimeException) {
                    // this could also happen
                }

                return try {
                    // Try to parse using a LocalDate pattern, assuming it follows a non-canonical format value.
                    // Example: 'yyyy-mm-dd' - the incoming data is a Date, but not our canonical date format.
                    val formatter = DateTimeFormatter.ofPattern(format ?: datetimePattern, Locale.ENGLISH)
                    LocalDate.parse(cleanedValue, formatter)
                    null
                } catch (e: DateTimeParseException) {
                    if (nullifyValue) {
                        return null
                    } else {
                        InvalidDateMessage(cleanedValue, fieldMapping, format)
                    }
                }
            }
            Type.CODE -> {
                // First, prioritize use of a local $alt format, even if no value set exists.
                return if (format == altDisplayToken) {
                    if (toAltCode(cleanedValue) != null) null else
                        InvalidCodeMessage(cleanedValue, fieldMapping, format)
                } else {
                    if (valueSetRef == null) error("Schema Error: missing value set for $fieldMapping")
                    when (format) {
                        displayToken ->
                            if (valueSetRef.toCodeFromDisplay(cleanedValue) != null) null else
                                InvalidCodeMessage(cleanedValue, fieldMapping, format)
                        codeToken -> {
                            val values = altValues ?: valueSetRef.values
                            if (values.find { it.code == cleanedValue } != null) null else
                                InvalidCodeMessage(cleanedValue, fieldMapping, format)
                        }
                        else ->
                            if (valueSetRef.toNormalizedCode(cleanedValue) != null) null else
                                InvalidCodeMessage(cleanedValue, fieldMapping, format)
                    }
                }
            }
            Type.TELEPHONE -> {
                return try {
                    // parse can fail if the phone number is not correct, which feels like bad behavior
                    // this then causes a report level failure, not an element level failure
                    val number = phoneNumberUtil.parse(cleanedValue, "US")
                    if (!number.hasNationalNumber() || number.nationalNumber > 9999999999L)
                        InvalidPhoneMessage(cleanedValue, fieldMapping)
                    else
                        null
                } catch (ex: Exception) {
                    InvalidPhoneMessage(cleanedValue, fieldMapping)
                }
            }
            Type.POSTAL_CODE -> {
                // Let in all formats defined by http://www.dhl.com.tw/content/dam/downloads/tw/express/forms/postcode_formats.pdf
                return if (!Regex("^[A-Za-z\\d\\- ]{3,12}\$").matches(cleanedValue))
                    InvalidPostalMessage(cleanedValue, fieldMapping, format)
                else
                    null
            }
            Type.HD -> {
                when (format) {
                    null,
                    hdNameToken -> null
                    hdUniversalIdToken -> null
                    hdSystemToken -> null
                    hdCompleteFormat -> {
                        val parts = cleanedValue.split(hdDelimiter)
                        if (parts.size == 1 || parts.size == 3) null else UnsupportedHDMessage(format, fieldMapping)
                    }
                    else -> UnsupportedHDMessage(format, fieldMapping)
                }
            }
            Type.EI -> {
                when (format) {
                    null,
                    eiNameToken -> null
                    eiNamespaceIdToken -> null
                    eiSystemToken -> null
                    eiCompleteFormat -> {
                        val parts = cleanedValue.split(eiDelimiter)
                        if (parts.size == 1 || parts.size == 4) null else UnsupportedEIMessage(format, fieldMapping)
                    }
                    else -> UnsupportedEIMessage(format, fieldMapping)
                }
            }

            else -> null
        }
    }

    /**
     * Take a formatted value and turn into a normalized value stored in a report
     */
    fun toNormalized(formattedValue: String, format: String? = null): String {
        val cleanedFormattedValue = formattedValue.trim()
        if (cleanedFormattedValue.isEmpty()) return ""
        return when (type) {
            Type.BLANK -> ""
            Type.DATE -> {
                val normalDate = try {
                    LocalDate.parse(cleanedFormattedValue)
                } catch (e: DateTimeParseException) {
                    null
                } ?: try {
                    val formatter = DateTimeFormatter.ofPattern(format ?: datePattern, Locale.ENGLISH)
                    LocalDate.parse(cleanedFormattedValue, formatter)
                } catch (e: DateTimeParseException) {
                    null
                }
                    // the next six date validation patterns are valid date patterns that we have seen be
                    // manually entered into EMR systems, but are not consistent, so we cannot use the "format" param
                    ?: try {
                        validateManualDates(cleanedFormattedValue, false)
                    } catch (e: DateTimeParseException) {
                        null
                    } ?: try {
                    val optionalDateTime = variableDateTimePattern
                    val df = DateTimeFormatter.ofPattern(optionalDateTime)
                    val ta = df.parseBest(
                        cleanedFormattedValue,
                        OffsetDateTime::from,
                        LocalDateTime::from,
                        Instant::from,
                        LocalDate::from
                    )
                    LocalDate.from(ta)
                } catch (e: DateTimeParseException) {
                    // if this value can be nullified because it is badly formatted and optional, simply return a blank string
                    if (nullifyValue) {
                        return ""
                    } else {
                        error("Invalid date: '$cleanedFormattedValue' for element $fieldMapping")
                    }
                } catch (e: DateTimeException) {
                    // this shouldn't ever really happen because we can always extract local date from a date time
                    // but it's better to be more secure and transparent
                    error(
                        "Unable to parse '$cleanedFormattedValue' for " +
                            "element $fieldMapping because it was the wrong type."
                    )
                }
                normalDate.format(dateFormatter)
            }
            Type.DATETIME -> {
                try {
                    val normalDateTime = getDateTime(cleanedFormattedValue, format)
                    normalDateTime.format(datetimeFormatter)
                } catch (e: DateTimeParseException) {
                    // if this value can be nullified because it is badly formatted, simply return a blank string
                    if (nullifyValue) {
                        return ""
                    } else {
                        error("Invalid date: '$cleanedFormattedValue' for element $fieldMapping")
                    }
                } catch (e: DateTimeException) {
                    // this shouldn't ever really happen because we can always extract local date from a date time
                    // but it's better to be more secure and transparent
                    error(
                        "Unable to parse '$cleanedFormattedValue' for " +
                            "element $fieldMapping because it was the wrong type."
                    )
                }
            }
            Type.CODE -> {
                // First, prioritize use of a local $alt format, even if no value set exists.
                when (format) {
                    altDisplayToken ->
                        toAltCode(cleanedFormattedValue)
                            ?: error(
                                "Invalid code: '$cleanedFormattedValue' is not a display value in altValues set " +
                                    "for $fieldMapping"
                            )
                    codeToken ->
                        toCode(cleanedFormattedValue)
                            ?: error(
                                "Invalid code '$cleanedFormattedValue' is not a display value in valueSet " +
                                    "for $fieldMapping"
                            )
                    displayToken ->
                        valueSetRef?.toCodeFromDisplay(cleanedFormattedValue)
                            ?: error(
                                "Invalid code: '$cleanedFormattedValue' is not a display value " +
                                    "for element $fieldMapping"
                            )
                    else ->
                        valueSetRef?.toNormalizedCode(cleanedFormattedValue)
                            ?: error(
                                "Invalid code: '$cleanedFormattedValue' does not match any codes " +
                                    "for $fieldMapping"
                            )
                }
            }
            Type.TELEPHONE -> {
                val number = phoneNumberUtil.parse(cleanedFormattedValue, "US")
                if (!number.hasNationalNumber() || number.nationalNumber > 9999999999L)
                    error("Invalid phone number '$cleanedFormattedValue' for $fieldMapping")
                val nationalNumber = DecimalFormat("0000000000").format(number.nationalNumber)
                "${nationalNumber}$phoneDelimiter${number.countryCode}$phoneDelimiter${number.extension}"
            }
            Type.POSTAL_CODE -> {
                // Let in all formats defined by http://www.dhl.com.tw/content/dam/downloads/tw/express/forms/postcode_formats.pdf
                if (!Regex("^[A-Za-z\\d\\- ]{3,12}\$").matches(cleanedFormattedValue))
                    error("Input Error: invalid postal code '$cleanedFormattedValue' for $fieldMapping")
                cleanedFormattedValue.replace(" ", "")
            }
            Type.HD -> {
                when (format) {
                    null,
                    hdCompleteFormat -> {
                        parseHD(cleanedFormattedValue) // to check
                        cleanedFormattedValue
                    }
                    hdNameToken -> {
                        val hd = parseHD(cleanedFormattedValue)
                        hd.name
                    }
                    else -> error("Schema Error: invalid format value")
                }
            }
            Type.EI -> {
                when (format) {
                    null,
                    eiCompleteFormat -> {
                        parseEI(cleanedFormattedValue) // to check
                        cleanedFormattedValue
                    }
                    eiNameToken -> {
                        val ei = parseEI(cleanedFormattedValue)
                        ei.name
                    }
                    else -> error("Schema Error: invalid format value")
                }
            }
            else -> cleanedFormattedValue
        }
    }

    fun toNormalized(subValues: List<SubValue>): String {
        if (subValues.isEmpty()) return ""
        return when (type) {
            Type.HD -> {
                var name = ""
                var universalId = ""
                var universalIdSystem = "ISO"
                for (subValue in subValues) {
                    when (subValue.format) {
                        null,
                        hdCompleteFormat -> {
                            val hdFields = parseHD(subValue.value)
                            name = hdFields.name
                            if (hdFields.universalId != null) universalId = hdFields.universalId
                            if (hdFields.universalIdSystem != null) universalIdSystem = hdFields.universalIdSystem
                        }
                        hdNameToken -> {
                            name = subValue.value
                        }
                        hdUniversalIdToken -> {
                            universalId = subValue.value
                        }
                        hdSystemToken -> {
                            universalIdSystem = subValue.value
                        }
                    }
                }
                "$name$hdDelimiter$universalId$hdDelimiter$universalIdSystem"
            }
            Type.EI -> {
                var name = ""
                var namespace = ""
                var universalId = ""
                var universalIdSystem = "ISO"
                for (subValue in subValues) {
                    when (subValue.format) {
                        null,
                        eiCompleteFormat -> {
                            val eiFields = parseEI(subValue.value)
                            name = eiFields.name
                            if (eiFields.namespace != null) namespace = eiFields.namespace
                            if (eiFields.universalId != null) universalId = eiFields.universalId
                            if (eiFields.universalIdSystem != null) universalIdSystem = eiFields.universalIdSystem
                        }
                        eiNameToken -> {
                            name = subValue.value
                        }
                        eiNamespaceIdToken -> {
                            namespace = subValue.value
                        }
                        eiUniversalIdToken -> {
                            universalId = subValue.value
                        }
                        eiSystemToken -> {
                            universalIdSystem = subValue.value
                        }
                    }
                }
                "$name$eiDelimiter$namespace$eiDelimiter$universalId$eiDelimiter$universalIdSystem"
            }
            else -> TODO("unsupported type")
        }
    }

    fun toAltDisplay(code: String): String? {
        if (!isCodeType) error("Internal Error: asking for an altDisplay for a non-code type")
        if (altValues == null) error("Schema Error: missing alt values for $fieldMapping")
        val altValue = altValues.find { code.equals(it.code, ignoreCase = true) }
            ?: altValues.find { "*" == it.code }
        return altValue?.display
    }

    fun toAltCode(altDisplay: String): String? {
        if (!isCodeType) error("Internal Error: asking for an altDisplay for a non-code type")
        if (altValues == null) error("Schema Error: missing alt values for $fieldMapping")
        val altValue = altValues.find { altDisplay.equals(it.display, ignoreCase = true) }
            ?: altValues.find { "*" == it.display }
        return altValue?.code
    }

    /**
     * Convert a string [code] to the code in the element's valueset.
     * @return a code of null if the code is not found
     */
    fun toCode(code: String): String? {
        if (!isCodeType) error("Internal Error: asking for codeValue for a non-code type")
        // if there are alt values, use those, otherwise, use the valueSet
        val values = valueSetRef?.values ?: error("Unable to find a value set for $fieldMapping.")
        val codeValue = values.find {
            code.equals(it.code, ignoreCase = true) || code.equals(it.replaces, ignoreCase = true)
        } ?: values.find { "*" == it.code }
        return codeValue?.code
    }

    /**
     * Determines if an element needs to use a mapper given the [elementValue].
     * @return true if a mapper needs to be run
     */
    fun useMapper(elementValue: String?): Boolean {
        val overrideValue = mapperOverridesValue != null && mapperOverridesValue
        return mapperRef != null && (overrideValue || elementValue.isNullOrBlank())
    }

    /**
     * Determines if an element needs to use a default given the [elementValue].
     * @return true if a default needs to be used
     */
    fun useDefault(elementValue: String?): Boolean {
        val overrideValue = defaultOverridesValue != null && defaultOverridesValue
        return overrideValue || elementValue.isNullOrBlank()
    }

    /**
     * Determine the value for this element based on the schema configuration.  This function checks if a
     * mapper needs to be run or if a default needs to be applied.
     * @param allElementValues the values for all other elements which are updated as needed
     * @param schema the schema
     * @param defaultOverrides element name and value pairs of defaults that override schema defaults
     * @param itemIndex the index of the item from a report being processed
     * @param sender Sender who submitted the data.  Can be null if called at a point in code where its not known
     * @return a mutable set with the processed value or empty string
     */
    fun processValue(
        allElementValues: Map<String, String>,
        schema: Schema,
        defaultOverrides: Map<String, String> = emptyMap(),
        itemIndex: Int,
        sender: Sender? = null,
    ): ElementResult {
        check(itemIndex > 0) { "Item index was $itemIndex, but must be larger than 0" }
        val retVal = ElementResult(if (allElementValues[name].isNullOrEmpty()) "" else allElementValues[name]!!)
        if (useMapper(retVal.value) && mapperRef != null) {
            // This gets the required value names, then gets the value from mappedRows that has the data
            val args = mapperArgs ?: emptyList()
            val valueNames = mapperRef.valueNames(this, args)
            val valuesForMapper = valueNames.mapNotNull { elementName ->
                if (elementName.contains("$")) {
                    tokenizeMapperValue(elementName, itemIndex)
                } else {
                    val valueElement = schema.findElement(elementName)
                    if (valueElement != null && allElementValues.containsKey(elementName) &&
                        !allElementValues[elementName].isNullOrEmpty()
                    ) {
                        ElementAndValue(valueElement, allElementValues[elementName]!!)
                    } else {
                        null
                    }
                }
            }
            // Only overwrite an existing value if the mapper returns a string
            val mapperResult = mapperRef.apply(this, args, valuesForMapper, sender)
            val value = mapperResult.value
            if (!value.isNullOrBlank()) {
                retVal.value = value
            }

            // Add any errors or warnings.  Use warnings as errors for required fields.
            if (this.isOptional) {
                retVal.warnings.addAll(mapperResult.errors)
                retVal.warnings.addAll(mapperResult.warnings)
            } else if (mapperResult.errors.isNotEmpty()) {
                retVal.errors.addAll(mapperResult.errors)
                retVal.warnings.addAll(mapperResult.warnings)
            } else {
                retVal.errors.addAll(mapperResult.warnings)
            }
        }

        // Finally, add a default value or empty string to elements that still have a null value.
        // Confusing: default values can be provided in the URL ("defaultOverrides"), or in the schema, or both.
        // Normally, default values are only apply if the value is blank at this point in the code.
        // However, if the Element has defaultOverridesValue=true set, that forces this code to run.
        // todo get rid of defaultOverrides in the URL.  I think its always an empty map!
        if (useDefault(retVal.value)) {
            retVal.value = if (defaultOverrides.containsKey(name)) { // First the URL default is used if it exists.
                defaultOverrides[name] ?: ""
            } else if (!default.isNullOrBlank()) { // otherwise, use the default in the schema
                default
            } else {
                // Check for cardinality and force the value to be empty/blank.
                if (retVal.value.isNullOrBlank() && !isOptional) {
                    retVal.errors += MissingFieldMessage(fieldMapping)
                }
                ""
            }
        }

        return retVal
    }

    /**
     * Populates the value of a specialized mapper token, indicated by a $ prefix
     * @param elementName the token name
     * @param index optional int value used with the $index token
     */
    fun tokenizeMapperValue(elementName: String, index: Int = 0): ElementAndValue {
        val tokenElement = Element(elementName)
        var retVal = ElementAndValue(tokenElement, "")
        when {
            elementName == "\$index" -> {
                retVal = ElementAndValue(tokenElement, index.toString())
            }
            elementName == "\$currentDate" -> {
                val currentDate = LocalDate.now().format(dateFormatter)
                retVal = ElementAndValue(tokenElement, currentDate)
            }
            elementName.contains("\$mode:") -> {
                retVal = ElementAndValue(tokenElement, elementName.split(":")[1])
            }
            elementName.contains("\$string:") -> {
                retVal = ElementAndValue(tokenElement, elementName.split(":")[1])
            }
        }

        return retVal
    }

    /**
     * For checkForError and toNormalized methods
     * validates a date string based on known manually entered formats into EMRs
     * @param formattedValue the date string that needs to be parsed/checked
     * @param returnNull is used for the checkForError method - this is expecting a null value if the date is valid
     * @return the formattedDate for methods like toNormalized
     */
    private fun validateManualDates(formattedValue: String, returnNull: Boolean = false): LocalDate? {
        // Cleanup the Date in variable values
        val cleanedDate = formattedValue.replace("-", "/")
        var formattedDate: LocalDate? = null

        manuallyEnteredDateFormats.forEach { dateFormat ->
            try {
                val formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.ENGLISH)
                formattedDate = LocalDate.parse(cleanedDate, formatter)
                // break out of the loop!
                return@forEach
            } catch (e: DateTimeParseException) {
                // continue to  the next try
            }
        }
        return if (returnNull && formattedDate != null) {
            null
        } else (
            if (returnNull && formattedDate == null) {
                // let it error out to bubble up to the next function
                LocalDate.parse(cleanedDate)
            } else {
                formattedDate
            }
            )
    }

    companion object {
        const val datePattern = "yyyyMMdd"
        const val datePatternMMddyyyy = "MMddyyyy"
        const val datetimePattern = "yyyyMMddHHmmZZZ"
        /** includes seconds  */
        const val highPrecisionDateTimePattern = "yyyyMMddHHmmss.SSSZZZ"
        // isn't she a beauty? This allows for all kinds of possible date time variations
        const val variableDateTimePattern = "[yyyyMMddHHmmssZ]" +
            "[yyyyMMddHHmmZ]" +
            "[yyyyMMddHHmmss][yyyy-MM-dd HH:mm:ss.ZZZ]" +
            "[yyyy-MM-dd[ H:mm:ss[.S[S][S]]]]" +
            "[yyyyMMdd[ H:mm:ss[.S[S][S]]]]" +
            "[M/d/yyyy[ H:mm[:ss[.S[S][S]]]]]" +
            "[yyyy/M/d[ H:mm[:ss[.S[S][S]]]]]"
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datePattern, Locale.ENGLISH)
        val datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern, Locale.ENGLISH)
        /** a higher precision date time formatter that includes seconds, and can be used */
        val highPrecisionDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
            highPrecisionDateTimePattern,
            Locale.ENGLISH
        )
        val manuallyEnteredDateFormats =
            arrayOf(datePattern, "M/d/yyyy", "MMddyyyy", "yyyy/M/d", "M/d/yyyy H:mm", "yyyy/M/d H:mm")
        const val displayToken = "\$display"
        const val caretToken = "\$code^\$display^\$system"
        const val codeToken = "\$code"
        const val systemToken = "\$system"
        const val altDisplayToken = "\$alt"
        const val areaCodeToken = "\$area"
        const val exchangeToken = "\$exchange"
        const val subscriberToken = "\$subscriber"
        const val countryCodeToken = "\$country"
        const val extensionToken = "\$extension"
        const val e164Token = "\$e164"
        const val defaultPhoneFormat = "\$area\$exchange\$subscriber"
        const val phoneDelimiter = ":"
        const val hdDelimiter = "^"
        const val hdNameToken = "\$name"
        const val hdUniversalIdToken = "\$universalId"
        const val hdSystemToken = "\$system"
        const val hdCompleteFormat = "\$complete"
        const val eiDelimiter = "^"
        const val eiNameToken = "\$name"
        const val eiNamespaceIdToken = "\$namespaceId"
        const val eiUniversalIdToken = "\$universalId"
        const val eiSystemToken = "\$system"
        const val eiCompleteFormat = "\$complete"
        val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
        const val zipFiveToken = "\$zipFive"
        const val zipFivePlusFourToken = "\$zipFivePlusFour"
        const val usZipFormat = """^(\d{5})[- ]?(\d{4})?$"""

        fun csvFields(name: String, format: String? = null): List<CsvField> {
            return listOf(CsvField(name, format))
        }

        fun parseHD(value: String, maximumLength: Int? = null): HDFields {
            val parts = value.split(hdDelimiter)
            val namespace = parts[0].let {
                if (maximumLength == null || maximumLength > it.length) {
                    it
                } else {
                    it.substring(0, maximumLength)
                }
            }
            return when (parts.size) {
                3 -> HDFields(namespace, parts[1], parts[2])
                1 -> HDFields(namespace, universalId = null, universalIdSystem = null)
                else -> error("Internal Error: Invalid HD value '$value'")
            }
        }

        fun parseEI(value: String): EIFields {
            val parts = value.split(eiDelimiter)
            return when (parts.size) {
                4 -> EIFields(parts[0], parts[1], parts[2], parts[3])
                1 -> EIFields(parts[0], namespace = null, universalId = null, universalIdSystem = null)
                else -> error("Internal Error: Invalid EI value '$value'")
            }
        }

        /**
         * The getDateTime function return the OffsetDatetime.  If it can't parse, it will throw either
         * DateTimeParseException or DateTimeException.  Which allows the caller to catch the exception.
         * @param [cleanedFormattedValue] datetime value to be parsed.
         * @param [format] format to parse
         * @return [OffsetDateTime] the best parsed datetime value
         */
        fun getDateTime(cleanedFormattedValue: String, format: String?): OffsetDateTime {

            val dateTime = try {
                // Try an ISO pattern
                OffsetDateTime.parse(cleanedFormattedValue)
            } catch (e: DateTimeParseException) {
                null
            } ?: try {
                // Try a HL7 pattern
                val formatter = DateTimeFormatter.ofPattern(format ?: datetimePattern, Locale.ENGLISH)
                OffsetDateTime.parse(cleanedFormattedValue, formatter)
            } catch (e: DateTimeParseException) {
                null
            } ?: try {
                // Try to parse using a LocalDate pattern assuming it is in our canonical dateFormatter. Central timezone.
                val date = LocalDate.parse(cleanedFormattedValue, dateFormatter)
                OffsetDateTime.of(date, LocalTime.of(0, 0), Environment.rsTimeZone)
            } catch (e: DateTimeParseException) {
                null
            } ?: try {
                // Try to parse using a LocalDate pattern, assuming it follows a non-canonical format value.
                // Example: 'yyyy-mm-dd' - the incoming data is a Date, but not our canonical date format.
                val formatter = DateTimeFormatter.ofPattern(format ?: datetimePattern, Locale.ENGLISH)
                val date = LocalDate.parse(cleanedFormattedValue, formatter)
                OffsetDateTime.of(date, LocalTime.of(0, 0), Environment.rsTimeZone)
            } catch (e: DateTimeParseException) {
                null
            } ?: try {
                getBestDateTime(cleanedFormattedValue, datePatternMMddyyyy)
            } catch (e: DateTimeParseException) {
                null
            } catch (e: DateTimeException) {
                null
            } ?: try {
                getBestDateTime(cleanedFormattedValue, variableDateTimePattern)
            } catch (e: DateTimeParseException) {
                throw DateTimeParseException(e.message, e.parsedString, e.errorIndex)
            } catch (e: DateTimeException) {
                throw DateTimeException(e.message)
            }

            return dateTime
        }

        /**
         * The getBestDateTime function parse to get the best match and return OffsetDatetime.
         * If it can't parse, it will throw either DateTimeParseException or DateTimeException.
         * Which allows the caller to catch the exception.
         * @param [value] datetime value to be parsed.
         * @param [optionalDateTime] format to parse
         * @return [OffsetDateTime] the best parsed datetime value
         */
        private fun getBestDateTime(value: String, optionalDateTime: String): OffsetDateTime {

            val df = DateTimeFormatter.ofPattern(optionalDateTime)
            val ta = df.parseBest(
                value,
                OffsetDateTime::from,
                LocalDateTime::from,
                Instant::from,
                LocalDate::from
            )
            // Using CENTRAL timezone here is inconsistent with other conversions, but changing to UTC
            // will cause issues to STLTs.
            val parsedValue = if (ta is LocalDateTime) {
                LocalDateTime.from(ta).atZone(ZoneId.of(USTimeZone.CENTRAL.zoneId)).toOffsetDateTime()
            } else {
                LocalDate.from(ta).atStartOfDay(ZoneId.of(USTimeZone.CENTRAL.zoneId)).toOffsetDateTime()
            }

            return parsedValue
        }

        /**
         * this looks to see if there is an "all zero offset" preceded by a plus sign on the
         * date time value. if there is, then we're going to flip this bit over to be
         * a negative offset. Note, according to the ISO-8601 specification, UTC is *NEVER*
         * supposed to be represented by `-0000`, only ever `+0000`. That said, RFC3339 does
         * offer the opportunity to use `-0000` to reflect an "unknown offset time", so it
         * is still valid and does parse. Also, Java understands and can parse from `-0000`
         * to `+0000`, so we are not breaking our implementation there.
         *
         * In addition to RFC3339 allowing for `-0000`, the HL7 spec allows for that value too,
         * so we should be good in a system that is HL7 compliant.
         *
         * RFC Link: https://datatracker.ietf.org/doc/html/rfc3339#section-4.3
         */
        fun convertPositiveOffsetToNegativeOffset(value: String): String {
            // look for the +0 offset
            val re = Regex(".+?\\+(00|0000|00:00)$")
            val match = re.find(value)
            // check to see if there is a match, if there isn't return the date as expected
            return when (match?.groups?.isNotEmpty()) {
                true -> {
                    // get the offset value at the end of the string
                    val offsetValue = match.groups.last()?.value
                    // if there's actually a match, and all of the values in the offset are zero
                    // because we only want to do this conversion IF the offset is zero. we never
                    // want to do this if the offset is some other value
                    if (offsetValue != null && offsetValue.all { it == '0' || it == ':' }) {
                        // create our replacement values
                        // I am doing it this way because it's possible that our desired level of
                        // precision for date time offset could change. I don't want my code to
                        // assume that it will always be +0000. +00 and +00:00 are also acceptable values,
                        // so I want this to be able to handle those options as well
                        val searchValue = "+$offsetValue"
                        val replaceValue = "-$offsetValue"
                        // replace the positive offset with the negative offset
                        value.replace(searchValue, replaceValue)
                    } else {
                        // we had an offset, but it's not what we expected, so just return the
                        // original value we were passed in to be safe
                        value
                    }
                }
                // the regex didn't match, so return the original value we passed in
                else -> value
            }
        }
    }
}

/**
 * A result for a given element with a [value] that may include [errors] or [warnings].
 */
data class ElementResult(
    var value: String?,
    val errors: MutableList<ActionLogDetail> = mutableListOf(),
    val warnings: MutableList<ActionLogDetail> = mutableListOf()
) {
    /**
     * Add an error [message] to the result.
     * @return the same instance of the result
     */
    fun error(message: ActionLogDetail) = apply {
        errors.add(message)
    }

    /**
     * Add a warning [message] to the result.
     * @return the same instance of the result
     */
    fun warning(message: ActionLogDetail) = apply {
        warnings.add(message)
    }
}