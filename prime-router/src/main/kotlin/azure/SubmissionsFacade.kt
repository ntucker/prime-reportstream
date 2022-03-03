package gov.cdc.prime.router.azure

import gov.cdc.prime.router.DetailActionLog
import gov.cdc.prime.router.DetailReport
import gov.cdc.prime.router.DetailedSubmissionHistory
import gov.cdc.prime.router.SubmissionHistory
import gov.cdc.prime.router.common.JacksonMapperUtilities
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Submissions / history API
 * Contains all business logic regarding submissions and JSON serialization.
 */
class SubmissionsFacade(
    private val dbSubmissionAccess: SubmissionAccess = DatabaseSubmissionsAccess(),
    private val dbAccess: DatabaseAccess = WorkflowEngine.databaseAccessSingleton
) {

    // Ignoring unknown properties because we don't require them. -DK
    private val mapper = JacksonMapperUtilities.datesAsTextMapper

    /**
     * Serializes a list of Actions into a String.
     *
     * @param organizationName from JWT Claim.
     * @param sortOrder sort the table by date in ASC or DESC order.
     * @param sortColumn sort the table by a specific column; defaults to sorting by created_at.
     * @param offset String representation of an OffsetDateTime used for paginating results.
     * @param pageSize Int of items to return per page.
     *
     * @return a String representation of an array of actions.
     */
    // Leaving separate from FindSubmissions to encapsulate json serialization
    fun findSubmissionsAsJson(
        organizationName: String,
        sortOrder: String,
        sortColumn: String,
        offset: OffsetDateTime?,
        toEnd: OffsetDateTime?,
        pageSize: Int
    ): String {
        val result = findSubmissions(organizationName, sortOrder, sortColumn, offset, toEnd, pageSize)
        return mapper.writeValueAsString(result)
    }

    private fun findSubmissions(
        organizationName: String,
        sortOrder: String,
        sortColumn: String,
        offset: OffsetDateTime?,
        toEnd: OffsetDateTime?,
        pageSize: Int,
    ): List<SubmissionHistory> {
        val order = try {
            SubmissionAccess.SortOrder.valueOf(sortOrder)
        } catch (e: IllegalArgumentException) {
            SubmissionAccess.SortOrder.DESC
        }

        val column = try {
            SubmissionAccess.SortColumn.valueOf(sortColumn)
        } catch (e: IllegalArgumentException) {
            SubmissionAccess.SortColumn.CREATED_AT
        }

        return findSubmissions(organizationName, order, column, offset, toEnd, pageSize)
    }

    /**
     * @param organizationName from JWT Claim.
     * @param sortOrder sort the table by date in ASC or DESC order; defaults to DESC.
     * @param sortColumn sort the table by a specific column; defaults to sorting by CREATED_AT.
     * @param offset String representation of an OffsetDateTime used for paginating results.
     * @param pageSize Int of items to return per page.
     *
     * @return a List of Actions
     */
    private fun findSubmissions(
        organizationName: String,
        sortOrder: SubmissionAccess.SortOrder,
        sortColumn: SubmissionAccess.SortColumn,
        offset: OffsetDateTime?,
        toEnd: OffsetDateTime?,
        pageSize: Int,
    ): List<SubmissionHistory> {
        require(organizationName.isNotBlank()) {
            "Invalid organization."
        }
        require(pageSize > 0) {
            "pageSize must be a positive integer."
        }

        val submissions = dbSubmissionAccess.fetchActions(
            organizationName,
            sortOrder,
            sortColumn,
            offset,
            toEnd,
            pageSize,
            SubmissionHistory::class.java
        )
        return submissions
    }

    fun findSubmission(
        organizationName: String,
        submissionId: Long,
    ): DetailedSubmissionHistory? {

        val submission = dbSubmissionAccess.fetchAction(
            organizationName,
            submissionId,
            DetailedSubmissionHistory::class.java,
            DetailReport::class.java,
            DetailActionLog::class.java,
        )

        submission?.let {
            val relatedSubmissions = dbSubmissionAccess.fetchRelatedActions(
                submission.actionId,
                DetailedSubmissionHistory::class.java,
                DetailReport::class.java,
                DetailActionLog::class.java,
            )
            it.enrichWithDescendants(relatedSubmissions)
        }

        return submission
    }

    /**
     * Find a [reportId] for a given [organizationName].
     * @return the detailed submission history or null if the report was not found
     */
    fun findReport(
        organizationName: String,
        reportId: UUID,
    ): DetailedSubmissionHistory? {
        val submissionId = dbAccess.fetchActionIdForReport(reportId)
        return if (submissionId != null) findSubmission(organizationName, submissionId)
        else null
    }

    companion object {

        // The SubmissionFacade is heavy-weight object (because it contains a Jackson Mapper) so reuse it when possible
        val common: SubmissionsFacade by lazy {
            SubmissionsFacade(DatabaseSubmissionsAccess())
        }
    }
}