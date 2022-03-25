import React, { ReactElement } from "react";
import { Navigate } from "react-router-dom";
import { useOktaAuth } from "@okta/okta-react";

import { permissionCheck } from "../webreceiver-utils";
import { PERMISSIONS } from "../resources/PermissionsResource";

interface AuthRouteProps {
    element: ReactElement;
    authLevel: PERMISSIONS;
}

export const AuthGate = ({ element, authLevel }: AuthRouteProps) => {
    const { authState } = useOktaAuth();
    const isAuthorized = permissionCheck(authLevel, authState);
    const isAdmin = permissionCheck(PERMISSIONS.PRIME_ADMIN, authState);

    return (
        <>
            {isAuthorized || isAdmin ? (
                { element }
            ) : (
                <Navigate to={{ pathname: "/" }} />
            )}
        </>
    );
};
