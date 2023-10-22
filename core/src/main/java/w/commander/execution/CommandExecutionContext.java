package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
public interface CommandExecutionContext {

    CommandSender sender();

    RawCommandArguments rawArguments();

    CommandExecutor executor();

    void answer(String text);

}
