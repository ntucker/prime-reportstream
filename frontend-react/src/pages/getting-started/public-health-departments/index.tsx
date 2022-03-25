import { SideNav } from "@trussworks/react-uswds";
import { NavLink, Navigate, Route, Routes } from "react-router-dom";

import { CODES, ErrorPage } from "../../error/ErrorPage";

import { PhdOverview } from "./Overview";
import { ELRChecklist } from "./ElrChecklist";
import { DataDownloadGuide } from "./DataDownloadGuide";

export const GettingStartedPublicHealthDepartments = () => {
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
            to={`/elr-checklist`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            ELR onboarding checklist
        </NavLink>,
        <NavLink
            to={`/data-download-guide`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Data download website guide
        </NavLink>,
    ];

    return (
        <>
            <section className="border-bottom border-base-lighter margin-bottom-6">
                <div className="grid-container">
                    <div className="grid-row grid-gap">
                        <div className="tablet:grid-col-12 margin-bottom-05">
                            <h1 className=" text-ink mobile:padding-top-0">
                                <span className="text-base">
                                    Getting started
                                </span>
                                <br /> Public health departments
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
                            <Route
                                path={"/"}
                                element={<Navigate to={"/overview"} />}
                            />
                            <Route
                                path={`/overview`}
                                element={<PhdOverview />}
                            />
                            <Route
                                path={`/elr-checklist`}
                                element={<ELRChecklist />}
                            />
                            <Route
                                path={`/data-download-guide`}
                                element={<DataDownloadGuide />}
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
