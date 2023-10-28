package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;

/**
 * @author whilein
 */
public interface ExecutionContextFactory {

    @NotNull ExecutionContext create(@NotNull CommandActor actor, @NotNull CommandExecutor executor, @NotNull RawArguments arguments);

}
