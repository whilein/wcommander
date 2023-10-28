package w.commander.tests

import w.commander.CommandSender

/**
 * @author whilein
 */
object TestCommandSender : CommandSender {
    override fun name(): String = "test"

    override fun sendMessage(text: String?) {
        // no-op
    }
}