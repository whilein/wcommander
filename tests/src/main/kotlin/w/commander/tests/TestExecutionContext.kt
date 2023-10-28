package w.commander.tests

import w.commander.CommandSender
import w.commander.RawArguments
import w.commander.execution.AbstractExecutionContext
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */

class TestExecutionContext(
        sender: CommandSender,
        executor: CommandExecutor,
        rawArguments: RawArguments
) : AbstractExecutionContext(sender, executor, rawArguments)

