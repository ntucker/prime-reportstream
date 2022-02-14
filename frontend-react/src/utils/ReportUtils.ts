import { Report } from "../network/HistoryApi";

// This function returns a list of unique senders of any ReportResource[]
export function getUniqueReceiverSvc(
    reports: Report[] | undefined
): Set<string> {
    return new Set(reports?.map((r) => r.receivingOrgSvc)) || new Set<string>();
}
