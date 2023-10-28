package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.CommandSender;
import w.commander.RawArguments;

/**
 * @author whilein
 */
public interface ExecutionContext {

    @NotNull CommandSender getSender();

    @NotNull RawArguments getRawArguments();

    @NotNull CommandExecutor getExecutor();

    void answer(String text);

}
