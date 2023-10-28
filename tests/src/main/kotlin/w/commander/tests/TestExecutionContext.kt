package w.commander.tests

import w.commander.CommandActor
import w.commander.RawArguments
import w.commander.execution.AbstractExecutionContext
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */

class TestExecutionContext(
        actor: CommandActor,
        executor: CommandExecutor,
        rawArguments: RawArguments
) : AbstractExecutionContext(actor, executor, rawArguments)

