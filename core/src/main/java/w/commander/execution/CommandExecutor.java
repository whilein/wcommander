package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
public interface CommandExecutor {

    void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    );

    default boolean isYielding() {
        return false;
    }

}
