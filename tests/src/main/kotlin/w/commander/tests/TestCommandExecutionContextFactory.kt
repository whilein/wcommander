package w.commander.tests

import w.commander.CommandSender
import w.commander.RawCommandArguments
import w.commander.execution.CommandExecutionContext
import w.commander.execution.CommandExecutionContextFactory
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */
class TestCommandExecutionContextFactory : CommandExecutionContextFactory {
    override fun create(
            sender: CommandSender,
            executor: CommandExecutor,
            arguments: RawCommandArguments
    ): CommandExecutionContext {
        return TestCommandExecutionContext(sender, executor, arguments)
    }
}

