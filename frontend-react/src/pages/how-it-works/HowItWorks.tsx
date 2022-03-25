import { SideNav } from "@trussworks/react-uswds";
import { NavLink, Navigate, Route, Routes } from "react-router-dom";

import { CODES, ErrorPage } from "../error/ErrorPage";

import { SecurityPractices } from "./SecurityPractices";
import { WhereWereLive } from "./WhereWereLive";
import { SystemsAndSettings } from "./SystemsAndSettings";
import { About } from "./About";

export const HowItWorks = () => {
    const itemsMenu = [
        <NavLink
            to={`/about`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            About
        </NavLink>,
        <NavLink
            to={`/where-were-live`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Where we're live
        </NavLink>,
        <NavLink
            to={`/systems-and-settings`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Systems and settings
        </NavLink>,
        <NavLink
            to={`/security-practices`}
            className={(isActive) =>
                isActive ? "usa-current" : "usa-nav__link"
            }
        >
            Security practices
        </NavLink>,
    ];

    return (
        <>
            <section className="grid-container tablet:margin-top-6 margin-bottom-5">
                <div className="grid-row grid-gap">
                    <div className="tablet:grid-col-4 margin-bottom-6">
                        <SideNav items={itemsMenu} />
                    </div>
                    <div className="tablet:grid-col-8 usa-prose rs-documentation">
                        <Routes>
                            <Route
                                path={"/"}
                                element={<Navigate to={"/about"} />}
                            />
                            <Route path={`/about`} element={<About />} />
                            <Route
                                path={`/where-were-live`}
                                element={<WhereWereLive />}
                            />
                            <Route
                                path={`/systems-and-settings`}
                                element={<SystemsAndSettings />}
                            />
                            <Route
                                path={`/security-practices`}
                                element={<SecurityPractices />}
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
