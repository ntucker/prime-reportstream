package gov.cdc.prime.router.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import gov.cdc.prime.router.AS2TransportType
import gov.cdc.prime.router.FileSettings
import gov.cdc.prime.router.transport.AS2Transport
import java.util.UUID

class DirectSendCommand : CliktCommand(
    name = "direct-send",
    help = "Using local credentials and configurations, send a file directly to through a transport to a receiver"
) {
    private val inputFile by option(
        "-i", "--input-file",
        help = "Input file to be sent to the output",
        metavar = "<file>"
    ).file(mustExist = true, mustBeReadable = true, canBeSymlink = true, canBeDir = false).required()

    private val receiverNameOption by option(
        "--receiver-name",
        metavar = "<org-name>.<receiver.name>",
        help = "Receiver in local settings. Must be a AS2 transport"
    ).required()

    private val verbose by option(
        "-v", "--verbose",
        help = "Verbose logging of each HTTP operation to the console"
    ).flag(default = false)

    private val silent by option(
        "-s", "--silent",
        help = "Do not echo progress or prompt for confirmation"
    ).flag(default = false)

    /**
     * Run the command
     */
    override fun run() {
        try {
            val settings = FileSettings("./settings")
            val receiver = settings.findReceiver(receiverNameOption) ?: abort("Could not find receiver")
            when (receiver.transport) {
                is AS2TransportType -> sendViaAs2(receiver.transport)
                else -> abort("Unsupported transport type")
            }
        } catch (e: PrintMessage) {
            // PrintMessage is the standard way to exit a command
            throw e
        } catch (e: Exception) {
            abort("CLI Internal Error: ${e.message}")
        }
    }

    private fun sendViaAs2(as2Info: AS2TransportType) {
        val transport = AS2Transport()
        val credentials = transport.lookupCredentials(receiverNameOption)
        transport.sendViaAS2(as2Info, credentials, receiverNameOption, UUID.randomUUID(), inputFile.readBytes())
    }

    /**
     * Echo verbose information to the console respecting the --silent and --verbose flag
     */
    private fun echo(message: String) {
        if (!silent) TermUi.echo(message)
    }

    /**
     * Echo verbose information to the console respecting the --silent and --verbose flag
     */
    private fun verbose(message: String) {
        if (verbose) TermUi.echo(message)
    }

    /**
     * Abort the program with the message
     */
    private fun abort(message: String): Nothing {
        throw PrintMessage(message, error = true)
    }
}