package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface CommandExecutor {

    void execute(@NotNull ExecutionContext context, @NotNull Consumer<@NotNull Result> callback);

    default boolean isOverrideable() {
        return false;
    }

}
