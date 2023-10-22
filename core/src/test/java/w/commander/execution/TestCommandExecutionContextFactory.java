package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
public class TestCommandExecutionContextFactory implements CommandExecutionContextFactory {
    @Override
    public CommandExecutionContext create(CommandSender sender, CommandExecutor executor, RawCommandArguments arguments) {
        return new TestCommandExecutionContext(sender, executor, arguments);
    }
}
