package w.commander.tests

import w.commander.CommandSender
import w.commander.RawCommandArguments
import w.commander.execution.AbstractCommandExecutionContext
import w.commander.execution.CommandExecutor

/**
 * @author whilein
 */

class TestCommandExecutionContext(
        sender: CommandSender,
        executor: CommandExecutor,
        rawArguments: RawCommandArguments
) : AbstractCommandExecutionContext(sender, executor, rawArguments)

