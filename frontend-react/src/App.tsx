import { GovBanner } from "@trussworks/react-uswds";
import { Route, Routes, useNavigate } from "react-router-dom";
import { OktaAuth, toRelativeUrl } from "@okta/okta-auth-js";
import { LoginCallback, Security } from "@okta/okta-react";
import { isIE } from "react-device-detect";
import { useIdleTimer } from "react-idle-timer";
import React, { Suspense } from "react";
import { NetworkErrorBoundary } from "rest-hooks";
import { ToastContainer } from "react-toastify";

import { Home } from "./pages/home/Home";
import { ReportStreamFooter } from "./components/ReportStreamFooter";
import Daily from "./pages/daily/Daily";
import { HowItWorks } from "./pages/how-it-works/HowItWorks";
import { Details } from "./pages/details/Details";
import { Login } from "./pages/Login";
import { TermsOfService } from "./pages/TermsOfService";
import { ReportStreamHeader } from "./components/header/ReportStreamHeader";
import { oktaAuthConfig } from "./oktaConfig";
import { AuthGate } from "./components/AuthGate";
import { PERMISSIONS } from "./resources/PermissionsResource";
import { permissionCheck, reportReceiver } from "./webreceiver-utils";
import { Upload } from "./pages/Upload";
import { CODES, ErrorPage } from "./pages/error/ErrorPage";
import { logout } from "./utils/UserUtils";
import TermsOfServiceForm from "./pages/tos-sign/TermsOfServiceForm";
import Spinner from "./components/Spinner";
import Submissions from "./pages/submissions/Submissions";
import { GettingStartedPublicHealthDepartments } from "./pages/getting-started/public-health-departments";
import { GettingStartedTestingFacilities } from "./pages/getting-started/testing-facilities";
import { AdminMain } from "./pages/admin/AdminMain";
import { AdminOrgEdit } from "./pages/admin/AdminOrgEdit";
import { EditReceiverSettings } from "./components/Admin/EditReceiverSettings";
import { EditSenderSettings } from "./components/Admin/EditSenderSettings";
import "react-toastify/dist/ReactToastify.css";
import SubmissionDetails from "./pages/submissions/SubmissionDetails";
import { NewSetting } from "./components/Admin/NewSetting";
import { FeatureFlagUIComponent } from "./pages/misc/FeatureFlags";
import SenderModeBanner from "./components/SenderModeBanner";
import SessionProvider from "./contexts/SessionStorageContext";

const OKTA_AUTH = new OktaAuth(oktaAuthConfig);

const App = () => {
    // This is for sanity checking and can be removed
    console.log(
        `process.env.REACT_APP_CLIENT_ENV='${
            process.env?.REACT_APP_CLIENT_ENV || "missing"
        }'`
    );
    const navigate = useNavigate();
    const customAuthHandler = (): void => {
        navigate("../login");
    };
    const handleIdle = (): void => {
        logout(OKTA_AUTH);
    };
    const restoreOriginalUri = async (_oktaAuth: any, originalUri: string) => {
        // check if the user would have any data to receive via their organizations from the okta claim
        // direct them to the /upload page if they do not have an organization that receives data
        const authState = OKTA_AUTH.authStateManager._authState;
        if (
            authState &&
            !reportReceiver(authState) &&
            permissionCheck(PERMISSIONS.SENDER, authState)
        ) {
            navigate(
                toRelativeUrl(
                    `${window.location.origin}/upload`,
                    window.location.origin
                )
            );
            return;
        }
        navigate(toRelativeUrl(originalUri, window.location.origin));
    };

    useIdleTimer({
        timeout: 1000 * 60 * 15,
        onIdle: handleIdle,
        debounce: 500,
    });

    if (isIE) return <ErrorPage code={CODES.UNSUPPORTED_BROWSER} />;
    return (
        <Security
            oktaAuth={OKTA_AUTH}
            onAuthRequired={customAuthHandler}
            restoreOriginalUri={restoreOriginalUri}
        >
            <Suspense fallback={<Spinner size={"fullpage"} />}>
                <NetworkErrorBoundary
                    fallbackComponent={() => <ErrorPage type="page" />}
                >
                    <SessionProvider>
                        <GovBanner aria-label="Official government website" />
                        <SenderModeBanner />
                        <ReportStreamHeader />
                        {/* Changed from main to div to fix weird padding issue at the top
                            caused by USWDS styling | 01/22 merged styles from .content into main, don't see padding issues anymore? */}
                        <main id="main-content">
                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route
                                    path="/how-it-works"
                                    element={<HowItWorks />}
                                />
                                <Route
                                    path="/terms-of-service"
                                    element={<TermsOfService />}
                                />
                                <Route path="/login" element={<Login />} />
                                <Route
                                    path="/login/callback"
                                    element={<LoginCallback />}
                                />
                                <Route
                                    path="/sign-tos"
                                    element={<TermsOfServiceForm />}
                                />
                                <Route
                                    path="/getting-started/public-health-departments"
                                    element={
                                        <GettingStartedPublicHealthDepartments />
                                    }
                                />
                                <Route
                                    path="/getting-started/testing-facilities"
                                    element={
                                        <GettingStartedTestingFacilities />
                                    }
                                />
                                <Route
                                    path="/daily-data"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.RECEIVER}
                                            element={<Daily />}
                                        />
                                    }
                                />
                                <Route
                                    path="/upload"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.SENDER}
                                            element={<Upload />}
                                        />
                                    }
                                />
                                <Route
                                    path="/submissions/:actionId"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.SENDER}
                                            element={<SubmissionDetails />}
                                        />
                                    }
                                />
                                <Route
                                    path="/submissions"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.SENDER}
                                            element={<Submissions />}
                                        />
                                    }
                                />
                                <Route
                                    path="/admin/settings"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<AdminMain />}
                                        />
                                    }
                                />
                                <Route
                                    path="/admin/orgsettings/org/:orgname"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<AdminOrgEdit />}
                                        />
                                    }
                                />
                                <Route
                                    path="/admin/orgreceiversettings/org/:orgname/receiver/:receivername/action/:action"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<EditReceiverSettings />}
                                        />
                                    }
                                />
                                <Route
                                    path="/admin/orgsendersettings/org/:orgname/sender/:sendername/action/:action"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<EditSenderSettings />}
                                        />
                                    }
                                />
                                <Route
                                    path="/admin/orgnewsetting/org/:orgname/settingtype/:settingtype"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<NewSetting />}
                                        />
                                    }
                                />
                                <Route
                                    path="/report-details"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.RECEIVER}
                                            element={<Details />}
                                        />
                                    }
                                />
                                <Route
                                    path="/features"
                                    element={
                                        <AuthGate
                                            authLevel={PERMISSIONS.PRIME_ADMIN}
                                            element={<FeatureFlagUIComponent />}
                                        />
                                    }
                                />
                                {/* Handles any undefined route */}
                                <Route
                                    element={
                                        <ErrorPage code={CODES.NOT_FOUND_404} />
                                    }
                                />
                            </Routes>
                        </main>
                        <ToastContainer limit={4} />
                        <footer className="usa-identifier footer">
                            <ReportStreamFooter />
                        </footer>
                    </SessionProvider>
                </NetworkErrorBoundary>
            </Suspense>
        </Security>
    );
};

export default App;
