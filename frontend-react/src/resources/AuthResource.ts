import { Resource } from "@rest-hooks/rest";

import {
    getStoredOktaToken,
    getStoredOrg,
} from "../contexts/SessionStorageTools";

export default class AuthResource extends Resource {
    pk(parent?: any, key?: string): string | undefined {
        throw new Error("Method not implemented.");
    }

    static useFetchInit = (init: RequestInit): RequestInit => {
        return {
            ...init,
            headers: {
                ...init.headers,
                Authorization: `Bearer ${getStoredOktaToken() || ""}`,
                Organization: getStoredOrg() || "",
                "Authentication-Type": "okta",
            },
        };
    };
}
