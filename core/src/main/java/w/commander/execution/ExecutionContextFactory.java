package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawArguments;

/**
 * @author whilein
 */
public interface ExecutionContextFactory {

    ExecutionContext create(CommandSender sender, CommandExecutor executor, RawArguments arguments);

}
