import axios from "axios";
import { getStoredOktaToken, getStoredOrg } from "../components/GlobalContextProvider";

export interface Endpoint {
    url: string,
    api: typeof BasicApi
}

export abstract class BasicApi {
    /*
        Keeping these out of component code, as well, keeps things
        pretty clean. Happy devs, happy Kev!
    */
    static accessToken = getStoredOktaToken();
    static organization = getStoredOrg();

    /*
        The general idea is an instance per API since headers may vary.
        This is a default that can be overridden 
    */
    static instance = axios.create({
        baseURL: `${process.env.REACT_APP_BACKEND_URL}`,
        headers: {
            Authorization: `Bearer ${this.accessToken}`,
            Organization: this.organization,
        },
        responseType: 'json',
    })

    /* 
        Meant to serve as the endpoint generator for each API class
    */
    abstract generateEndpoint(urlParam: string): Endpoint;
}
