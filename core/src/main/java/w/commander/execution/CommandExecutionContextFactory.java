package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
public interface CommandExecutionContextFactory {

    CommandExecutionContext create(CommandSender sender, CommandExecutor executor, RawCommandArguments arguments);

}
