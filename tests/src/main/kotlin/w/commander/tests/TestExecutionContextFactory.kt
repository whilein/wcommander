package w.commander.tests

import w.commander.CommandActor
import w.commander.RawArguments
import w.commander.execution.ExecutionContext
import w.commander.execution.ExecutionContextFactory
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */
class TestExecutionContextFactory : ExecutionContextFactory {
    override fun create(
            actor: CommandActor,
            executor: CommandExecutor,
            arguments: RawArguments
    ): ExecutionContext {
        return TestExecutionContext(actor, executor, arguments)
    }
}

