
### Schema:         non-standard/all-in-one-health-ca-covid-19
#### Description:   all-in-one-health - CSV lab report schema

---

**Name**: Language

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Notes

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Ok To Contact Patient

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Patient County

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Provider Facility Name

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Result

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Specimen Site

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Test Name

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

This field is ignored.

---

**Name**: Date Reported

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

---

**Name**: equipment_model_name

**Type**: TABLE

**PII**: No

**Cardinality**: [0..1]


**Reference URL**:
[https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification](https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification) 

**Table**: LIVD-SARS-CoV-2-2021-04-28

**Table Column**: Model

---

**Name**: Facility CLIA

**Type**: ID_CLIA

**PII**: No

**Cardinality**: [0..1]

---

**Name**: Accession Number

**Type**: ID

**PII**: No

**HL7 Fields**

- [OBR-3-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.3.1)
- [ORC-3-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.3.1)
- [SPM-2-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.2)

**Cardinality**: [0..1]

**Documentation**:

Accension number

---

**Name**: message_id

**Type**: ID

**PII**: No

**Cardinality**: [1..1]

**Documentation**:

unique id to track the usage of the message

---

**Name**: Date Test Ordered

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

---

**Name**: Facility City

**Type**: CITY

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The city of the facility which the test was ordered from

---

**Name**: Facility Name

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The name of the facility which the test was ordered from

---

**Name**: Facility Phone

**Type**: TELEPHONE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The phone number of the facility which the test was ordered from

---

**Name**: Facility State

**Type**: TABLE

**PII**: No

**Cardinality**: [1..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:

The state of the facility which the test was ordered from

---

**Name**: Facility Street Address

**Type**: STREET

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The address of the facility which the test was ordered from

---

**Name**: Facility Zip

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The zip code of the facility which the test was ordered from

---

**Name**: Provider City

**Type**: CITY

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The city of the provider

---

**Name**: Provider First Name

**Type**: PERSON_NAME

**PII**: No

**HL7 Fields**

- [OBR-16-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.16.3)
- [ORC-12-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.12.3)

**Cardinality**: [0..1]

**Documentation**:

The first name of the provider who ordered the test

---

**Name**: Provider ID/ NPI

**Type**: ID_NPI

**PII**: No

**HL7 Fields**

- [OBR-16-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.16.1)
- [ORC-12-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.12.1)

**Cardinality**: [0..1]

**Documentation**:

The ordering provider’s National Provider Identifier

---

**Name**: Provider Last Name

**Type**: PERSON_NAME

**PII**: No

**HL7 Fields**

- [OBR-16-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.16.2)
- [ORC-12-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.12.2)

**Cardinality**: [0..1]

**Documentation**:

The last name of provider who ordered the test

---

**Name**: Provider Phone Number

**Type**: TELEPHONE

**PII**: Yes

**HL7 Fields**

- [OBR-17](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.17)
- [ORC-14](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.14)

**Cardinality**: [0..1]

**Documentation**:

The phone number of the provider

---

**Name**: Provider State

**Type**: TABLE

**PII**: No

**Cardinality**: [0..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:

The state of the provider

---

**Name**: Provider Street Address

**Type**: STREET

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The street address of the provider

---

**Name**: Provider ZIP

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The zip code of the provider

---

**Name**: Patient City

**Type**: CITY

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The patient's city

---

**Name**: patient_county

**Type**: TABLE

**PII**: No

**Cardinality**: [1..1]

**Table**: zip-code-data

**Table Column**: county

---

**Name**: Patient Date Of Birth

**Type**: DATE

**PII**: Yes

**Format**: yyyyMMdd

**Cardinality**: [0..1]

**Documentation**:

The patient's date of birth. Default format is yyyyMMdd.

Other states may choose to define their own formats.


---

**Name**: Ethnicity

**Type**: CODE

**PII**: No

**Format**: $display

**Default Value**: U

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
H|Hispanic or Latino
H|Hispanic
H|Latino
H|Mex. Amer./Hispanic
H|H
N|Non Hispanic or Latino
N|Non Hispanic
N|Not Hispanic or Latino
N|Not Hispanic
N|N
U|Unknown
U|U
U|UNK
U|Black
U|White
U|African American
U|NULL
U|Patient Declines

**Documentation**:

Translate multiple inbound ethnicity values to RS / OMB values

---

**Name**: Patient First Name

**Type**: PERSON_NAME

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The patient's first name

---

**Name**: Patient Sex

**Type**: CODE

**PII**: No

**Format**: $display

**Default Value**: U

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
F|Female
F|F
M|Male
M|M
U|U
U|UNK
U|UNKNOWN
O|O
O|Other
O|OTH
A|A
A|Ambiguous

**Documentation**:

Translate multiple inbound Gender values to RS values

---

**Name**: Patient Identifier

**Type**: TEXT

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The ID for the patient within one of the reporting entities for this lab result. It could be the
the patient ID from the testing lab, the oder placer, the ordering provider, or even within the PRIME system itself.


---

**Name**: Ordering_facility_name

**Type**: HD

**PII**: No

**HL7 Fields**

- [PID-3-4](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.4)
- [PID-3-6-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.6.2)

**Cardinality**: [0..1]

**Documentation**:

The name of the assigner of the patient_id field. Typically we use the name of the ordering facility

---

**Name**: patient_id_type

**Type**: TEXT

**PII**: No

**Default Value**: PI

**Cardinality**: [0..1]

---

**Name**: Patient Last Name

**Type**: PERSON_NAME

**PII**: Yes

**Cardinality**: [1..1]

**Documentation**:

The patient's last name

---

**Name**: Patient Middle Initial

**Type**: PERSON_NAME

**PII**: Yes

**Cardinality**: [0..1]

---

**Name**: Patient Phone Number

**Type**: TELEPHONE

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The patient's phone number with area code

---

**Name**: Race

**Type**: CODE

**PII**: No

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
1002-5|American Indian or Alaska Native
2028-9|Asian
2054-5|Black or African American
2076-8|Native Hawaiian or Other Pacific Islander
2106-3|White
2131-1|Other
UNK|Unknown
ASKU|Asked, but unknown

**Documentation**:

The patient's race. There is a common valueset defined for race values, but some states may choose to define different code/value combinations.


---

**Name**: Patient State

**Type**: TABLE

**PII**: No

**Cardinality**: [1..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:

The patient's state

---

**Name**: Patient Street Address

**Type**: STREET

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:

The patient's street address

---

**Name**: Patient Zip

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The patient's zip code

---

**Name**: Specimen ID

**Type**: ID

**PII**: No

**HL7 Fields**

- [OBR-2-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.2.1)
- [ORC-2-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.2.1)

**Cardinality**: [0..1]

**Documentation**:

The ID number of the lab order from the placer

---

**Name**: processing_mode_code

**Type**: CODE

**PII**: No

**Default Value**: P

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
D|Debugging
P|Production
T|Training

**Documentation**:

P, D, or T for Production, Debugging, or Training

---

**Name**: Facility CLIA

**Type**: ID_CLIA

**PII**: No

**HL7 Fields**

- [MSH-4-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/MSH.4.2)
- [PID-3-4-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.4.2)
- [PID-3-6-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.6.2)
- [SPM-2-1-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.1.3)
- [SPM-2-2-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.2.3)

**Cardinality**: [0..1]

**Documentation**:

The reporting facility's CLIA

---

**Name**: Facility Name

**Type**: TEXT

**PII**: No

**HL7 Fields**

- [MSH-4-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/MSH.4.1)
- [PID-3-4-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.4.1)
- [PID-3-6-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.6.1)
- [SPM-2-1-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.1.2)
- [SPM-2-2-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.2.2)

**Cardinality**: [0..1]

**Documentation**:

The reporting facility's name

---

**Name**: sender_id

**Type**: TEXT

**PII**: No

**Default Value**: all-in-one-health-ca

**Cardinality**: [0..1]

**Documentation**:

ID name of org that is sending this data to ReportStream.  Suitable for provenance or chain of custody tracking.  Not to be confused with sending_application, in which ReportStream acts as the 'sender' to the downstream jurisdiction.

---

**Name**: Specimen Collection Date

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**HL7 Fields**

- [OBR-7](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.7)
- [OBR-8](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.8)
- [OBX-14](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.14)
- [SPM-17-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.17.1)

**Cardinality**: [0..1]

**Documentation**:

The date which the specimen was collected. The default format is yyyyMMddHHmmsszz


---

**Name**: Specimen Type

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
445297001|Swab of internal nose
258500001|Nasopharyngeal swab
871810001|Mid-turbinate nasal swab
697989009|Anterior nares swab
258411007|Nasopharyngeal aspirate
429931000124105|Nasal aspirate
258529004|Throat swab
119334006|Sputum specimen
119342007|Saliva specimen
258607008|Bronchoalveolar lavage fluid sample
119364003|Serum specimen
119361006|Plasma specimen
440500007|Dried blood spot specimen
258580003|Whole blood sample
122555007|Venous blood specimen
119297000|Blood specimen

**Documentation**:

The specimen source, such as Blood or Serum

---

**Name**: Device Identifier

**Type**: TEXT

**PII**: No

**Default Value**: 99999999

**Cardinality**: [0..1]


**Reference URL**:
[https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification](https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification) 

**Table**: LIVD-SARS-CoV-2-2021-08-11

**Table Column**: Testkit Name ID

**Documentation**:

Follows guidence for OBX-17 as defined in the HL7 Confluence page

---

**Name**: Test Code

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Table**: LIVD-SARS-CoV-2-2021-08-11

**Table Column**: Test Performed LOINC Code

**Documentation**:

The LOINC code of the test performed. This is a standardized coded value describing the test

---

**Name**: Result Code

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
260373001|Detected
260415000|Not detected
720735008|Presumptive positive
10828004|Positive
42425007|Equivocal
260385009|Negative
895231008|Not detected in pooled specimen
462371000124108|Detected in pooled specimen
419984006|Inconclusive
125154007|Specimen unsatisfactory for evaluation
455371000124106|Invalid result
840539006|Disease caused by sever acute respiratory syndrome coronavirus 2 (disorder)
840544004|Suspected disease caused by severe acute respiratory coronavirus 2 (situation)
840546002|Exposure to severe acute respiratory syndrome coronavirus 2 (event)
840533007|Severe acute respiratory syndrome coronavirus 2 (organism)
840536004|Antigen of severe acute respiratory syndrome coronavirus 2 (substance)
840535000|Antibody to severe acute respiratory syndrome coronavirus 2 (substance)
840534001|Severe acute respiratory syndrome coronavirus 2 vaccination (procedure)
373121007|Test not done

**Documentation**:

The result of the test performed. For IgG, IgM and CT results that give a numeric value put that here.

---

**Name**: Result Date

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

---

**Name**: Date Reported

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

---

**Name**: Test_result_status

**Type**: CODE

**PII**: No

**Default Value**: F

**HL7 Fields**

- [OBR-25-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.25.1)
- [OBX-11-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.11.1)

**Cardinality**: [0..1]

**Value Sets**

Code | Display
---- | -------
A|Some, but not all, results available
C|Corrected, final
F|Final results
I|No results available; specimen received, procedure incomplete
M|Corrected, not final
N|Procedure completed, results pending
O|Order received; specimen not yet received
P|Preliminary
R|Results stored; not yet verified
S|No results available; procedure scheduled, but not done
X|No results available; Order canceled
Y|No order on record for this test
Z|No record of this patient

**Documentation**:

The test result status, which is different from the test result itself. Per the valueset, this indicates if
the test result is in some intermediate status, is a correction, or is the final result.


---

**Name**: Facility State

**Type**: CITY

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The city of the testing lab

---

**Name**: Facility CLIA

**Type**: ID_CLIA

**PII**: No

**HL7 Fields**

- [OBR-2-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.2.3)
- [OBR-3-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.3.3)
- [OBX-15-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.15.1)
- [OBX-23-10](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.23.10)
- [ORC-2-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.2.3)
- [ORC-3-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.3.3)

**Cardinality**: [1..1]

**Documentation**:

CLIA Number from the laboratory that sends the message to DOH

An example of the ID is 03D2159846


---

**Name**: Facility CLIA

**Type**: ID

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

Typically this will be the same as the `testing_lab_clia`, but potentially could not be.

---

**Name**: Facility Name

**Type**: TEXT

**PII**: No

**HL7 Fields**

- [OBR-2-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.2.2)
- [OBR-3-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.3.2)
- [OBX-15-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.15.2)
- [OBX-23-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.23.1)
- [ORC-2-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.2.2)
- [ORC-3-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.3.2)
- [PID-3-4-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.4.1)

**Cardinality**: [0..1]

**Documentation**:

The name of the laboratory which performed the test, can be the same as the sending facility name

---

**Name**: Facility Phone

**Type**: TELEPHONE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The phone number of the testing lab

---

**Name**: Specimen Received Date

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

**Documentation**:

The received date time for the specimen. This field is very important to many states for their HL7,
but for most of our senders, the received date time is the same as the collected date time. Unfortunately,
setting them to the same time breaks many validation rules. Most ELR systems apparently look for them to
be offset, so this field takes the `specimen_collection_date_time` field and offsets it by a small amount.


---

**Name**: Facility Zip

**Type**: TABLE

**PII**: No

**Cardinality**: [0..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:

The state for the testing lab

---

**Name**: Facility Street Address

**Type**: STREET

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The street address for the testing lab

---

**Name**: Facility Zip

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:

The postal code for the testing lab

---