package gov.cdc.prime.router.azure

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import gov.cdc.prime.router.ActionError
import gov.cdc.prime.router.ActionLog
import gov.cdc.prime.router.Receiver
import gov.cdc.prime.router.Report
import gov.cdc.prime.router.Sender
import gov.cdc.prime.router.messages.PreviewMessage
import gov.cdc.prime.router.messages.PreviewResponseMessage
import gov.cdc.prime.router.tokens.OktaAuthentication
import org.apache.logging.log4j.kotlin.Logging

class PreviewFunction(
    private val oktaAuthentication: OktaAuthentication = OktaAuthentication(PrincipalLevel.SYSTEM_ADMIN),
    private val workflowEngine: WorkflowEngine = WorkflowEngine()
) : Logging {
    /**
     * The preview end-point does a translation of the input message to the output payload.
     */
    @FunctionName("preview")
    fun preview(
        @HttpTrigger(
            name = "preview",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "preview"
        ) request: HttpRequestMessage<String?>
    ): HttpResponseMessage {
        return oktaAuthentication.checkAccess(request) {
            try {
                val parameters = checkRequest(request)
                val response = processRequest(parameters)
                val body = mapper.writeValueAsString(response)
                HttpUtilities.okResponse(request, body)
            } catch (ex: IllegalArgumentException) {
                HttpUtilities.badRequestResponse(request, ex.message ?: "")
            } catch (ex: Exception) {
                logger.error("Internal error for ${it.userName} request: $ex")
                HttpUtilities.internalErrorResponse(request)
            }
        }
    }

    /**
     * Encapsulate the parameters of the functions.
     */
    data class FunctionParameters(
        val previewMessage: PreviewMessage,
        val receiver: Receiver,
        val sender: Sender,
    )

    /**
     * Throw an [IllegalArgumentException] with a message based on [message], [errors] and [warnings].
     */
    private fun badRequest(
        message: String,
        errors: List<ActionLog> = emptyList(),
        warnings: List<ActionLog> = emptyList()
    ): Nothing {
        val previewErrorMessage = PreviewResponseMessage.Error(
            message,
            errors.map { it.toSummary() },
            warnings.map { it.toSummary() }
        )
        val response = mapper.writeValueAsString(previewErrorMessage)
        throw IllegalArgumentException(response)
    }

    /**
     * Look at the request body.
     * Check for valid values with defaults are assumed if not specified.
     */
    fun checkRequest(request: HttpRequestMessage<String?>): FunctionParameters {
        val body = request.body
            ?: badRequest("Missing body")
        val previewMessage = mapper.readValue(body, PreviewMessage::class.java)
        val receiver = previewMessage.receiver
            ?: workflowEngine.settings.findReceiver(previewMessage.receiverName)
            ?: badRequest("Missing receiver")
        val sender = previewMessage.sender
            ?: workflowEngine.settings.findSender(previewMessage.senderName)
            ?: badRequest("Missing sender")
        return FunctionParameters(previewMessage, receiver, sender)
    }

    /**
     * Main logic of the Azure function. Useful for unit testing.
     */
    fun processRequest(parameters: FunctionParameters): PreviewResponseMessage.Success {
        val warnings = mutableListOf<ActionLog>()
        return readReport(parameters, warnings)
            .translate(parameters, warnings)
            .buildResponse(parameters, warnings)
    }

    /**
     * Read and parse the preview reports
     */
    private fun readReport(parameters: FunctionParameters, warnings: MutableList<ActionLog>): Report {
        return try {
            val readResult = workflowEngine.parseReport(
                parameters.sender,
                parameters.previewMessage.inputContent,
                emptyMap()
            )
            warnings.addAll(readResult.actionLogs.warnings)
            readResult.report
        } catch (ae: ActionError) {
            badRequest("Unable to parse input report", errors = ae.details)
        }
    }

    /**
     * Translate a report into a preview report
     */
    private fun Report.translate(parameters: FunctionParameters, warnings: MutableList<ActionLog>): Report {
        val routedReport = workflowEngine.translator.filterAndTranslateForReceiver(
            input = this,
            defaultValues = emptyMap(),
            receiver = parameters.receiver,
            warnings = warnings
        ) ?: badRequest("Unable to translate the report. May not match filters", warnings = warnings)
        return routedReport.report
    }

    /**
     * Build a preview response from a report and warnings
     */
    private fun Report.buildResponse(
        parameters: FunctionParameters,
        warnings: List<ActionLog>
    ): PreviewResponseMessage.Success {
        val content = String(workflowEngine.blob.createBodyBytes(this))
        val externalName = Report.formFilename(
            id,
            schema.name,
            bodyFormat,
            createdDateTime,
            parameters.receiver.translation,
            workflowEngine.metadata
        )
        return PreviewResponseMessage.Success(
            receiverName = parameters.receiver.fullName,
            externalFileName = externalName,
            content = content,
            warnings = warnings.map { it.toSummary() }
        )
    }

    /**
     * Summarize the [ActionLog]
     */
    private fun ActionLog.toSummary(): String {
        return "$detail"
    }

    companion object {
        private val mapper = jacksonMapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
            .registerModule(JavaTimeModule())
    }
}