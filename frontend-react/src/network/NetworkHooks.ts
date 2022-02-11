import { useState, useEffect } from "react";
import { Endpoint } from "./BasicApi";
import { Report } from "./HistoryApi";

interface NetworkHookProps {
    endpoint: Endpoint
}

export const useNetwork = (endpoint: Endpoint): any[] => {
    /* TODO: How can we do this all with generics? */
    const [data, setData] = useState<Report[]>()

    useEffect(() => {
        /* Fetch data and handle any parsing needed */
    }, [])

    if (!data) return []
    return data
}
