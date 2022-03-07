import { Endpoint } from "../api/Api";

import { Api } from "./Api";

jest.mock("../../components/GlobalContextProvider");

describe("Api.ts", () => {
    test("static members have values", () => {
        expect(Api.accessToken).toBe("test token");
        expect(Api.organization).toBe("test org");
        expect(Api.baseUrl).toBe("/api");
    });

    /* TODO: Test axios config somehow? */

    test("generateEndpoint() creates valid endpoint", () => {
        const testEndpoint: Endpoint = Api.generateEndpoint("/test/url", Api);
        expect(testEndpoint.url).toBe("/test/url");
        expect(testEndpoint.api).toBe(Api);
    });
});