enum Jurisdiction {
    FEDERAL = "FEDERAL",
    STATE = "STATE",
    COUNTY = "COUNTY",
}

enum Format {
    CSV = "text/csv",
    HL7 = "application/hl7-v2",
}

enum CustomerStatus {
    INACTIVE = "inactive",
    TESTING = "testing",
    ACTIVE = "active",
}

enum ProcessingType {
    SYNC = "sync",
    ASYNC = "async",
}

enum ReportStreamFilterDefinition {
    BY_COUNTY = "filterByCounty",
    MATCHES = "matches",
    NO_MATCH = "doesNotMatch",
    EQUALS = "orEquals",
    VALID_DATA = "hasValidDataFor",
    AT_LEAST_ONE = "hasAtLeastOneOf",
    ALLOW_ALL = "allowAll",
    ALLOW_NONE = "allowNone",
    VALID_CLIA = "isValidCLIA",
    DATE_INTERVAL = "inDateInterval",
}

enum BatchOperation {
    NONE = "NONE",
    MERGE = "MERGE",
}

enum EmptyOperation {
    NONE = "NONE",
    SEND = "SEND",
}

enum USTimeZone {
    PACIFIC = "US/Pacific",
    MOUNTAIN = "US/Mountain",
    ARIZONA = "US/Arizona",
    CENTRAL = "US/Central",
    EASTERN = "US/Eastern",
    SAMOA = "US/Samoa",
    HAWAII = "US/Hawaii",
    EAST_INDIANA = "US/East-Indiana",
    INDIANA_STARKE = "US/Indiana-Starke",
    MICHIGAN = "US/Michigan",
    CHAMORRO = "Pacific/Guam",
}

type ReportStreamSettingsEnum =
    | "jurisdiction"
    | "format"
    | "customerStatus"
    | "reportStreamFilterDefinition";

const getListOfEnumValues = (e: ReportStreamSettingsEnum): string[] => {
    switch (e) {
        case "customerStatus":
            return Array.from(Object.values(CustomerStatus));
        case "format":
            return Array.from(Object.values(Format));
        case "jurisdiction":
            return Array.from(Object.values(Jurisdiction));
        case "reportStreamFilterDefinition":
            return Array.from(Object.values(ReportStreamFilterDefinition));
    }
};

abstract class SampleObject {
    stringify() {
        return JSON.stringify(this, null, 6);
    }
    abstract getAllEnums(): Map<string, string[]>;
    abstract description(): string;
}

class SampleFilterObject extends SampleObject {
    filters = [
        {
            topic: "covid-19",
            jurisdictionalFilter: [],
            qualityFilter: [],
            routingFilter: [],
            processingModeFilter: [],
        },
    ];

    stringify(): string {
        return JSON.stringify(this.filters, null, 6);
    }

    getAllEnums(): Map<string, string[]> {
        return new Map<string, string[]>([
            [
                "Available Filters",
                Array.from(Object.values(ReportStreamFilterDefinition)),
            ],
        ]);
    }

    description(): string {
        return "This field takes an array of filter objects (see object above).";
    }
}

class SampleJwkSet {
    scope = "scope";
    keys = {
        kty: "",
        use: "",
        keyOps: "",
        alg: "",
        x5u: "",
        x5c: "",
        x5t: "",
        n: "",
        e: "",
        d: "",
        crv: "",
        p: "",
        q: "",
        dp: "",
        dq: "",
        qi: "",
        x: "",
        y: "",
        k: "",
    };
}

class SampleKeysObj extends SampleObject {
    listOfKeys = [new SampleJwkSet()];
    stringify(): string {
        return JSON.stringify(this.listOfKeys, null, 6);
    }
    getAllEnums(): Map<string, string[]> {
        return new Map(); // Currently doesn't require any enums
    }
    description(): string {
        return "This field takes an array of JwkSets (see above).";
    }
}

class SampleTimingObj extends SampleObject {
    initialTime = "00:00";
    maxReportCount = 365;
    numberPerDay = 1;
    operation = BatchOperation.MERGE;
    timeZone = USTimeZone.ARIZONA;
    whenEmpty = {
        action: EmptyOperation.NONE,
        onlyOncePerDay: true,
    };

    getAllEnums(): Map<string, string[]> {
        return new Map<string, string[]>([
            ["operation", Array.from(Object.values(BatchOperation))],
            ["timeZone", Array.from(Object.values(USTimeZone))],
            ["whenEmpty.action", Array.from(Object.values(EmptyOperation))],
        ]);
    }

    description(): string {
        return "This field takes a timing object (see above).";
    }
}

class SampleTranslationObj extends SampleObject {
    defaults = new Map<string, string>([["", ""]]);
    format = Format.CSV;
    nameFormat = "";
    receivingOrganization = "xx_phd";
    schemaName = "schema";

    getAllEnums(): Map<string, string[]> {
        return new Map<string, string[]>([
            ["format", Array.from(Object.values(Format))],
        ]);
    }

    description(): string {
        return "This field takes a translation object (see above).";
    }
}

export {
    Jurisdiction,
    Format,
    ProcessingType,
    CustomerStatus,
    SampleFilterObject,
    SampleKeysObj,
    SampleTranslationObj,
    SampleTimingObj,
    SampleObject,
    getListOfEnumValues,
};