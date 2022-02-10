import axios from 'axios'
import { getStoredOktaToken, getStoredOrg } from '../components/GlobalContextProvider';

export type Report = {
    sent: number,
    via: string,
    total: number,
    fileType: string,
    type: string,
    reportId: string,
    expires: number,
    sendingOrg: string,
    receivingOrg: string,
    receivingOrgSvc: string,
    facilities: string[],
    actions: Action[],
    displayName: string,
    content: string,
    fieldName: string,
    mimeType: string
}

export type Action = {
    date: string,
    user: string,
    action: string | undefined
}

/* 
    Enumerated endpoints keeps things tidy in each API interface we build
*/
enum Endpoint {
    REPORTS = '/api/history/report'
}

/* 
    Keeping these out of component code, as well, keeps things
    pretty clean. Happy devs, happy Kev!
*/
const accessToken = getStoredOktaToken();
const organization = getStoredOrg();

/* 
    The general idea is an instance per API since headers may vary.
    
    We could also set baseURL and response type globally? Though,
    we have to consider all CRUD operations; do we ever get anything
    but JSON back? A file, maybe?
*/
const historyApi = axios.create({
    baseURL: `${process.env.REACT_APP_BACKEND_URL}`,
    headers: {
        Authorization: `Bearer ${accessToken}`,
        Organization: organization,
    },
    responseType: 'json',
})

/* 
    Gets list of reports for the organization designated in the
    `historyApi` headers.

    @returns Report[]
*/
const getReports = async () => {
    const response = await historyApi.get<Report[]>(Endpoint.REPORTS)
    const reports = response.data
    return reports
}
