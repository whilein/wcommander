package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.result.Result;
import w.commander.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author whilein
 */
public abstract class AbstractCommandExecutor implements CommandExecutor {

    public final void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        // todo conditions

        doExecute(context, callback);
    }

    protected abstract void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    );

}
