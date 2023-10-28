package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
public interface CommandExecutionContext {

    CommandSender getSender();

    RawCommandArguments getRawArguments();

    CommandExecutor getExecutor();

    void answer(String text);

}
