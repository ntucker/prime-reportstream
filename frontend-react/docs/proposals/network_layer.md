
# React Network Layer

## Brief
For all of its confusion, `coinbase/rest-hooks` has given us quite a complex framework to configure API interfaces that run via the `useResource()` hook. I admire the way it is structured, but I do not enjoy the sparse documentation and buggy testing tools it provides. For this, and more, I propose a similar structure to our bespoke networking layer. The goal is to have our interfacing needs handled by class-based API configurations, while the network hooks (i.e. the `useNetwork()` hook) handle fetching and caching as needed. This will result in easy-to-configure-and-read classes and a single-line implementation in components. This also prevents components from housing any network variables or logic, keeping them cleaner and easier to maintain. Oh, and did I mention it will be equally as type-safe?!

## The `Api` abstract class
To configure our global API defaults and provide helpful methods, we can set up an abstract `Api` class. Extending this give you access to static members (i.e. auth token, Axios instance, etc.) while allowing you to override them in the case of a new api using varied headers, auth, etc.

```typescript
export interface Endpoint {
    url: string;
    api: typeof Api;
}

export abstract class Api {
    // Member variables you might want to access in other API modules
    static accessToken: string = getStoredOktaToken();
    static organization: string = getStoredOrg();
    static baseUrl: string = "/api";

    // Our base instance with standard API headers
    static instance: AxiosInstance = axios.create({
        baseURL: `${process.env.REACT_APP_BACKEND_URL}`,
        headers: {
            Authorization: `Bearer ${this.accessToken}`,
            Organization: this.organization,
        },
        responseType: "json",
    });

    // Helper method to generate consistent endpoints across API modules
    static generateEndpoint(urlParam: string, api: typeof Api): Endpoint {
        return {
            url: urlParam,
            api: api,
        };
    }
}
```

## API Modules
To consolidate all our types, classes, endpoints, etc. it makes sense to take a modular approach to developing new api integrations. 

```typescript
export class Report {
    // Shape and defaults defined here for data type
}

export class HistoryApi extends Api {
    // Overridden url
    static baseUrl: string = "/api/history/report";

    // Endpoints with custom naming. I used list and detail since
    // we are familiar with those through rest-hooks.
    static list = (): Endpoint => {
        return HistoryApi.generateEndpoint(this.baseUrl, this);
    };

    static detail = (reportId: string): Endpoint => {
        return HistoryApi.generateEndpoint(
            `${this.baseUrl}/${reportId}`,
            this
        );
    };
}
```

## Implement an API module
The best part of our current networking library is the integration of hooks. I want to maintain this functionality in our own solution. The most basic implementation would be a `useNetwork<T>()` hook that allows us to cast a type, interface, or class to the result, enforcing our defined types and resorting to defaults where necessary.

```typescript
export function useNetwork<T>({url, api}: Endpoint): T {
    const [data, setData] = useState<T>();

    useEffect(() => {
        /* Fetch data and handle any parsing needed */
        api.instance
            .get<T>(url)
            .then((res) => console.log(res))
            .catch((err) => {
                throw Error(err);
            });
    }, []);

    if (!data) throw Error("Error fetching data! Uh oh.");
    return data;
}
```
This allows us to maintain a single-line use inside React components like so:
```typescript
const reports: Reports[] = useNetwork<Reports[]>(HistoryApi.list())
```

## Improvements
- `Memoize/cache`: After we ensure the network hook is performing the call and casting to our desired type, we can introduce memoization/caching to improve our speed recall and the rate at which we call the API.
- `Errors`: Not sure how I want to handle errors yet. We can take the error boundary approach, but that pattern can get murky when nesting layers and layers of components. We can try to standardize where we place boundaries (i.e. at page level, at subcomponent level, etc.)