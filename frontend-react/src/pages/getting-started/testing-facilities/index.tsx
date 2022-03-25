import { SideNav } from "@trussworks/react-uswds";
import { NavLink, Navigate, Route, Routes } from "react-router-dom";

import { CODES, ErrorPage } from "../../error/ErrorPage";

import { FacilitiesOverview } from "./Overview";
import { AccountRegistrationGuide } from "./AccountRegistrationGuide";
import { CsvUploadGuide } from "./CsvUploadGuide";
import { CsvSchemaDocumentation } from "./CsvSchemaDocumentation";

export const GettingStartedTestingFacilities = () => {
    const itemsMenu = [
        <NavLink
            to={`/overview`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Overview
        </NavLink>,
        <NavLink
            to={`/account-registration-guide`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Account registration guide
        </NavLink>,
        <NavLink
            to={`/csv-upload-guide`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            CSV upload guide
        </NavLink>,
        <NavLink
            to={`/csv-schema`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            CSV schema documentation
        </NavLink>,
    ];

    return (
        <>
            <section className="border-bottom border-base-lighter margin-bottom-6">
                <div className="grid-container">
                    <div className="grid-row grid-gap">
                        <div className="tablet:grid-col-12 margin-bottom-05">
                            <h1 className="text-ink">
                                <span className="text-base">
                                    Getting started
                                </span>
                                <br /> Organizations and testing facilities
                            </h1>
                        </div>
                    </div>
                </div>
            </section>
            <section className="grid-container margin-bottom-5">
                <div className="grid-row grid-gap">
                    <div className="tablet:grid-col-4 margin-bottom-6">
                        <SideNav items={itemsMenu} />
                    </div>
                    <div className="tablet:grid-col-8 usa-prose">
                        <Routes>
                            {/* Handles anyone going to /getting-started without extension */}
                            <Route path={"/"}>
                                <Navigate replace to={`/overview`} />
                            </Route>
                            <Route
                                path={`/overview`}
                                element={FacilitiesOverview}
                            />
                            <Route
                                path={`/account-registration-guide`}
                                element={AccountRegistrationGuide}
                            />
                            <Route
                                path={`/csv-upload-guide`}
                                element={CsvUploadGuide}
                            />
                            <Route
                                path={`/csv-schema`}
                                element={CsvSchemaDocumentation}
                            />
                            {/* Handles any undefined route */}
                            <Route
                                element={
                                    <ErrorPage code={CODES.NOT_FOUND_404} />
                                }
                            />
                        </Routes>
                    </div>
                </div>
            </section>
        </>
    );
};
