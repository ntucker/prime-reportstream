import axios from 'axios'
import { getStoredOktaToken, getStoredOrg } from '../components/GlobalContextProvider';
import { BasicApi, Endpoint } from './BasicApi';

/* 
    Using classes allows us to keep some easy defaults when constructing
    an object from a JSON response. This is meant to mimic the behavior of
    rest-hooks defaults.
*/
export class Report {
    sent: number = -1
    via: string = ""
    total: number = -1
    fileType: string = ""
    type: string = ""
    reportId: string = ""
    expires: number = -1
    sendingOrg: string = ""
    receivingOrg: string = ""
    receivingOrgSvc: string = ""
    facilities: string[] = []
    actions: Action[] = []
    displayName: string = ""
    content: string = ""
    fieldName: string = ""
    mimeType: string = ""
}

export class Action {
    date: string = ""
    user: string = ""
    action: string | undefined
}

/* Enumerated endpoints keeps things tidy in each API interface we build */
enum HistoryEndpoints {
    REPORT_BASE = '/api/history/report',
}

export class HistoryApi extends BasicApi {

    /*
        TODO: How can we make this private static AND enforce it when
        extending BasicApi? 
    */
    generateEndpoint(urlParam: string): Endpoint {
        return {
            url: urlParam,
            api: HistoryApi
        }
    }

    static list = (): Endpoint => {
        return new HistoryApi().generateEndpoint(HistoryEndpoints.REPORT_BASE)
    }

    static detail = (reportId: string): Endpoint => {
        return new HistoryApi().generateEndpoint(`${HistoryEndpoints.REPORT_BASE}/${reportId}`)
    }

}

