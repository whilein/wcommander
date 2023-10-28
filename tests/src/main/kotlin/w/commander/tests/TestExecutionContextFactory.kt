package w.commander.tests

import w.commander.CommandSender
import w.commander.RawArguments
import w.commander.execution.ExecutionContext
import w.commander.execution.ExecutionContextFactory
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */
class TestExecutionContextFactory : ExecutionContextFactory {
    override fun create(
            sender: CommandSender,
            executor: CommandExecutor,
            arguments: RawArguments
    ): ExecutionContext {
        return TestExecutionContext(sender, executor, arguments)
    }
}

