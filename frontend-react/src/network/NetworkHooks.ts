import axios from "axios";
import { useState, useEffect } from "react";

import { Api } from "./Api";

export interface Endpoint {
    url: string;
    api: typeof Api;
}

export function useNetwork<T>(endpoint: Endpoint): T {
    const [data, setData] = useState<T>();

    /* BUG: Why won't this hook run? */
    useEffect(() => {
        /* Fetch data and handle any parsing needed */
        // endpoint.api
        //     .instance(endpoint.url)
        //     .then((res) => console.log(res))
        //     .catch((err) => {
        //         throw Error(err);
        //     });
        axios.get<T>(endpoint.url, {
            headers: {
                Authorization: `Bearer ${Api.accessToken}`,
                Organization: Api.organization
            }
        }).then(res => setData(res.data))
    }, []);

    if (!data) throw Error("Error fetching data! Uh oh.");
    return data;
}
