import { Helmet } from "react-helmet";

import schema from "../../../content/getting_started_csv_upload.json";

/* eslint-disable jsx-a11y/anchor-has-content */
export const CsvSchemaDocumentation = () => {
    return (
        <>
            <Helmet>
                <title>
                    CSV schema documentation | Organizations and testing
                    facilities | Getting started | {process.env.REACT_APP_TITLE}
                </title>
            </Helmet>
            <section id="anchor-top">
                <span className="text-base text-italic">
                    Updated: January 2022
                </span>
                <h2 className="margin-top-0 ">
                    CSV schema documentation{" "}
                    <span className="text-secondary bg-white border-1px border-secondary font-body-3xs padding-x-1 padding-y-05 text-secondary margin-left-2 text-ttbottom">
                        Pilot program{" "}
                    </span>
                </h2>
                <p className="usa-intro text-base">
                    How to format data for submission to ReportStream via CSV
                    upload.
                </p>
                <p>
                The ReportStream standard CSV schema is a blend of the Department of Health and Human Science's (HHS) <a href="https://www.hhs.gov/coronavirus/testing/covid-19-diagnostic-data-reporting/index.html" target="_blank" className="usa-link">requirements for COVID-19 test data</a> as well as those of numerous jurisdictions. This standard schema will be accepted by state, tribal, local, or territorial (STLT) health departments partnered with ReportStream. </p><p> 
                <em>This documentation is related to <a href="/getting-started/testing-facilities/csv-upload-guide" className="usa-link">CSV upload</a>, a feature currently being piloted in
                            select jurisdictions with organizations or
                            facilities that have existing Electronic Medical
                            Record (EMR) systems. Pilot partners are selected by recommendation from jurisdictions. Find out if your jurisdiction is partnered with ReportStream and contact us to learn more. </em>
                    
                </p>

                <p>
                    <strong>In this guide</strong>
                </p>
                <ul>
                    {schema.fields.map((field, fieldIndex) => {
                        return (
                            <div key={`toc-${fieldIndex}`} className="">
                                {field.sections?.map(
                                    (section, sectionIndex) => {
                                        return (
                                            <li
                                                key={`toc-${fieldIndex}-${sectionIndex}`}
                                            >
                                                <a
                                                    href={`#${section.slug}`}
                                                    className="usa-link"
                                                >
                                                    {section.title}
                                                </a>
                                            </li>
                                        );
                                    }
                                )}
                            </div>
                        );
                    })}
                </ul>
                <p>
                    <strong>Resources</strong>
                </p>
                <ul>
                    <li>
                        <a
                            id="standard-csv"
                            href="/assets/csv/ReportStream-StandardCSV-ExampleData-20220107.csv"
                            className="usa-link"
                        >
                            ReportStream standard CSV with example data
                        </a>
                    </li>
                </ul>
                <div className="usa-alert usa-alert--info margin-y-6">
                    <div className="usa-alert__body">
                        <h3 className="usa-alert__heading font-body-md margin-top-05">
                            Notes on formatting your CSV
                        </h3>
                        <p >
                            <strong>Column headers and order</strong>
                            <ul>
                                <li>Column headers can can be placed in any order.</li>
                                <li>Column headers must be included as specified in this documentation. </li>
                            </ul>
                            
                        </p>
                        <p> <strong>Required, requested, and optional fields</strong></p>
                        <ul className="usa-alert__text">
                            <li><strong>Required:</strong> Files <em>must</em>{" "}
                            contain these column headers and values. If headers
                            or fields are blank or contain incorrect values you
                            will not be able to submit your file. Accepted
                            values are outlined for each header and field below.</li>
                            <li><strong>Requested:</strong> Fields are not required.
                            If available, including this data is incredibly
                            helpful to public health.</li>
                            <li><strong>Optional:</strong> Fields are not required.</li>
                        
                        </ul>
                        
                    </div>
                </div>
            </section>

            {schema.fields.map((field, fieldIndex) => {
                return (
                    <div
                        data-testid="fieldDiv"
                        key={`field-${fieldIndex}`}
                        className="margin-bottom-5"
                    >
                        {field.sections?.map((section, sectionIndex) => {
                            return (
                                <div
                                    key={`section-${fieldIndex}-${sectionIndex}`}
                                    className="border-top-1px border-ink margin-top-9"
                                >
                                    <h3
                                        id={`${section.slug}`}
                                        className="font-body-lg margin-y-1"
                                    >
                                        {section.title}
                                    </h3>

                                    {section.items?.map((item, itemIndex) => {
                                        return (
                                            <div
                                                key={`item-${fieldIndex}-${sectionIndex}-${itemIndex}`}
                                                className="margin-top-8 rs-documentation__values"
                                            >
                                                <h4
                                                    id={`doc-${item.colHeader}`}
                                                    className="font-body-md margin-bottom-2"
                                                >
                                                    {item.name}
                                                    {item.required ? (
                                                        <span className="text-normal bg-white border-1px border-secondary font-body-3xs padding-x-1 padding-y-05 text-secondary margin-left-2 text-ttbottom">
                                                            Required
                                                        </span>
                                                    ) : (
                                                        <span className="text-normal bg-white border-1px border-base font-body-3xs padding-x-1 padding-y-05 text-base margin-left-2 text-ttbottom">
                                                            Optional
                                                        </span>
                                                    )}
                                                    {section.title ===
                                                    "Ask on entry (AOE) data elements" ? (
                                                        <span className="text-normal bg-white border-1px border-base font-body-3xs padding-x-1 padding-y-05 text-base margin-left-2 text-ttbottom">
                                                            Requested
                                                        </span>
                                                    ) : null}
                                                </h4>
                                                <div className="margin-bottom-3">
                                                    {item.notes?.map(
                                                        (note, noteIndex) => {
                                                            return (
                                                                <p
                                                                    key={`value-${fieldIndex}-${sectionIndex}-${itemIndex}-${noteIndex}`}
                                                                    dangerouslySetInnerHTML={{
                                                                        __html: `${note}`,
                                                                    }}
                                                                ></p>
                                                            );
                                                        }
                                                    )}
                                                </div>
                                                <div className="grid-row margin-bottom-05">
                                                    <div className="grid-col-4 text-base">
                                                        Column header
                                                    </div>
                                                    <div className="grid-col-auto">
                                                        {item.colHeader}
                                                    </div>
                                                </div>
                                                <div className="grid-row margin-bottom-05 border-base-lighter border-top-1px padding-top-1">
                                                    <div className="grid-col-4 text-base">
                                                        Value type
                                                    </div>
                                                    <div className="grid-col-auto">
                                                        {item.valueType}
                                                    </div>
                                                </div>
                                                <div className="grid-row margin-bottom-05 border-base-lighter border-top-1px padding-top-1">
                                                    <div className="grid-col-4 text-base">
                                                        {item.acceptedFormat ? (
                                                            <>Accepted format</>
                                                        ) : null}
                                                        {item.acceptedValues ? (
                                                            <>
                                                                Accepted
                                                                value(s)
                                                            </>
                                                        ) : null}
                                                        {item.acceptedExample ? (
                                                            <>Example(s)</>
                                                        ) : null}
                                                    </div>
                                                    <ul className="grid-col-8 value-list">
                                                        {item.values?.map(
                                                            (
                                                                value,
                                                                valueIndex
                                                            ) => {
                                                                return (
                                                                    <li
                                                                        key={`value-${fieldIndex}-${sectionIndex}-${itemIndex}-${valueIndex}`}
                                                                        dangerouslySetInnerHTML={{
                                                                            __html: `${value}`,
                                                                        }}
                                                                    ></li>
                                                                );
                                                            }
                                                        )}
                                                    </ul>
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                            );
                        })}
                        <p className="margin-top-8">
                            <a href="#anchor-top" className="usa-link">
                                Return to top
                            </a>
                        </p>
                    </div>
                );
            })}
        </>
    );
};
