package gov.cdc.prime.router.azure

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.QueueTrigger
import com.microsoft.azure.functions.annotation.StorageAccount
import gov.cdc.prime.router.OrganizationService
import gov.cdc.prime.router.Report
import java.util.logging.Level

const val batch = "batch"
const val defaultBatchSize = 100

/**
 * Batch will find all the reports waiting to with a next "batch" action for a receiver name.
 * It will either send the reports directly or merge them together.
 */
class BatchFunction {
    @FunctionName(batch)
    @StorageAccount("AzureWebJobsStorage")
    fun run(
        @QueueTrigger(name = "message", queueName = batch)
        message: String,
        context: ExecutionContext,
    ) {
        try {
            context.logger.info("Batch message: $message")
            val workflowEngine = WorkflowEngine()
            val event = Event.parseQueueMessage(message) as ReceiverEvent
            if (event.eventAction != Event.EventAction.BATCH) {
                context.logger.warning("Batch function received a $message")
                return
            }
            val receiver = workflowEngine.metadata.findService(event.receiverName)
                ?: error("Internal Error: receiver name ${event.receiverName}")
            val maxBatchSize = receiver.batch?.maxReportCount ?: defaultBatchSize
            val actionHistory = ActionHistory(event.eventAction.toTaskAction(), context)
            actionHistory.trackActionParams(message)

            workflowEngine.handleReceiverEvent(event, maxBatchSize) { headers, txn ->
                if (headers.isEmpty()) {
                    context.logger.info("Batch: empty batch")
                    return@handleReceiverEvent
                } else {
                    context.logger.info("Batch contains ${headers.size} reports")
                }
                val inReports = headers.map {
                    val report = workflowEngine.createReport(it)
                    // todo replace the use of Event.Header.Task with info from ReportFile.
                    // todo also I think we don't need `sources` any more.
                    actionHistory.trackExistingInputReport(it.task.reportId)
                    report
                }
                val mergedReports = when (receiver.batch?.operation) {
                    OrganizationService.BatchOperation.MERGE -> listOf(Report.merge(inReports))
                    else -> inReports
                }
                val outReports = when (receiver.format) {
                    OrganizationService.Format.HL7 -> mergedReports.flatMap { it.split() }
                    else -> mergedReports
                }
                outReports.forEach {
                    val outReport = it.copy(destination = receiver)
                    val outEvent = ReportEvent(Event.EventAction.SEND, outReport.id)
                    workflowEngine.dispatchReport(outEvent, outReport, txn)
                    actionHistory.trackCreatedReport(outEvent, outReport, receiver)
                    context.logger.info("Batch: queued to send ${outEvent.toQueueMessage()}")
                }
                val msg = if (inReports.size == 1 && outReports.size == 1) "Success: No merging needed - batch of 1"
                else "Success: merged ${inReports.size} reports into ${outReports.size} reports"
                actionHistory.trackActionResult(msg)
                workflowEngine.recordAction(actionHistory, txn)
            }
        } catch (e: Exception) {
            context.logger.log(Level.SEVERE, "Batch exception", e)
        }
    }
}