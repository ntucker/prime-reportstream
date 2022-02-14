import React, { Suspense } from "react";
import { Helmet } from "react-helmet";
import { NetworkErrorBoundary, useController, useResource } from "rest-hooks";
import { RouteComponentProps } from "react-router-dom";
import { GridContainer, Grid, Button } from "@trussworks/react-uswds";

import HipaaNotice from "../../components/HipaaNotice";
import Spinner from "../../components/Spinner";
import { ErrorPage } from "../error/ErrorPage";
import OrgSettingsResource from "../../resources/OrgSettingsResource";
import { OrgSenderTable } from "../../components/Admin/OrgSenderTable";
import { OrgReceiverTable } from "../../components/Admin/OrgReceiverTable";
import {
    TextInputComponent,
    TextAreaComponent,
} from "../../components/Admin/AdminFormEdit";
import {
    showAlertNotification,
    showError,
} from "../../components/AlertNotifications";

type AdminOrgEditProps = {
    orgname: string;
};

export function AdminOrgEdit({
    match,
}: RouteComponentProps<AdminOrgEditProps>) {
    const orgname = match?.params?.orgname || "";
    const orgSettings: OrgSettingsResource = useResource(
        OrgSettingsResource.detail(),
        { orgname: orgname }
    );

    const { fetch: fetchController } = useController();
    const saveOrgData = async () => {
        try {
            const data = JSON.stringify(orgSettings);
            await fetchController(
                OrgSettingsResource.update(),
                { orgname },
                data
            );
            showAlertNotification(
                "success",
                `Item '${orgname}' has been updated`
            );
        } catch (e: any) {
            console.trace(e);
            showError(`Updating item '${orgname}' failed. ${e.toString()}`);
            return false;
        }

        return true;
    };

    return (
        <NetworkErrorBoundary
            fallbackComponent={() => <ErrorPage type="page" />}
        >
            <Helmet>
                <title>Admin | Org Edit | {process.env.REACT_APP_TITLE}</title>
            </Helmet>
            <section className="grid-container margin-bottom-5">
                <h2 className="margin-bottom-0">
                    <Suspense
                        fallback={
                            <span className="text-normal text-base">
                                Loading Info...
                            </span>
                        }
                    >
                        Org name:{" "}
                        {match?.params?.orgname || "missing param 'orgname'"}
                    </Suspense>
                </h2>
            </section>
            <NetworkErrorBoundary
                fallbackComponent={() => <ErrorPage type="message" />}
            >
                <Suspense fallback={<Spinner />}>
                    <section className="grid-container margin-top-0">
                        <GridContainer>
                            <Grid row>
                                <Grid col={3}>Meta:</Grid>
                                <Grid col={9}>
                                    {JSON.stringify(orgSettings?.meta) || {}}{" "}
                                    <br />
                                </Grid>
                            </Grid>
                            <TextInputComponent
                                fieldname={"description"}
                                label={"Description"}
                                defaultvalue={orgSettings.description}
                                savefunc={(v) => (orgSettings.description = v)}
                            />
                            <TextInputComponent
                                fieldname={"jurisdiction"}
                                label={"Jurisdiction"}
                                defaultvalue={orgSettings.jurisdiction}
                                savefunc={(v) => (orgSettings.jurisdiction = v)}
                            />
                            <TextInputComponent
                                fieldname={"countyName"}
                                label={"County Name"}
                                defaultvalue={orgSettings.countyName || null}
                                savefunc={(v) =>
                                    (orgSettings.countyName =
                                        v === "" ? null : v)
                                }
                            />
                            <TextInputComponent
                                fieldname={"stateCode"}
                                label={"State Code"}
                                defaultvalue={orgSettings.stateCode || null}
                                savefunc={(v) =>
                                    (orgSettings.stateCode =
                                        v === "" ? null : v)
                                }
                            />
                            <TextAreaComponent
                                fieldname={"filters"}
                                label={"Filters"}
                                defaultvalue={orgSettings.filters}
                                savefunc={(v) => (orgSettings.filters = v)}
                            />
                            <Grid row>
                                <Button
                                    form="edit-setting"
                                    type="submit"
                                    data-testid="submit"
                                    onClick={() => saveOrgData()}
                                >
                                    Save
                                </Button>
                            </Grid>
                        </GridContainer>

                        <br />
                    </section>
                    <OrgSenderTable orgname={orgname} />
                    <OrgReceiverTable orgname={orgname} />
                </Suspense>
            </NetworkErrorBoundary>
            <HipaaNotice />
        </NetworkErrorBoundary>
    );
}