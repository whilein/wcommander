package w.commander.tests

import w.commander.CommandActor

/**
 * @author whilein
 */
object TestCommandActor : CommandActor {
    override fun getName(): String = "test"

    override fun sendMessage(text: String) {
        // no-op
    }
}