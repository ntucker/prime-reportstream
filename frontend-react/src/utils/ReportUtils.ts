import { Report } from "../network/HistoryApi";
import ReportResource from "../resources/ReportResource";

// This function returns a list of unique senders of any ReportResource[]
export function getUniqueReceiverSvc(reports: Report[]): Set<string> {
    return new Set(reports.map((r) => r.receivingOrgSvc));
}
