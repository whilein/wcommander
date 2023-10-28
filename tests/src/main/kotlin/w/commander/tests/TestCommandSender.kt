package w.commander.tests

import w.commander.CommandSender

/**
 * @author whilein
 */
object TestCommandSender : CommandSender {
    override fun getName(): String = "test"

    override fun sendMessage(text: String) {
        // no-op
    }
}