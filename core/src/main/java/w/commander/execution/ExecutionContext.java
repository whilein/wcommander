package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;

/**
 * @author whilein
 */
public interface ExecutionContext {

    @NotNull CommandActor getActor();

    @NotNull RawArguments getRawArguments();

    @NotNull CommandExecutor getExecutor();

    void dispatch(String text);

}
