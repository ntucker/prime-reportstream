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

const accessToken = getStoredOktaToken();
const organization = getStoredOrg();

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
    const response = await historyApi.get<Report[]>('/api/history/report')
    const reports = response.data
    return reports
}
