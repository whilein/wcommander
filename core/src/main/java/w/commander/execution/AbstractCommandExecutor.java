package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public abstract class AbstractCommandExecutor implements CommandExecutor {

    public final void execute(
            @NotNull ExecutionContext context,
            @NotNull Consumer<@NotNull Result> callback
    ) {
        // todo conditions

        doExecute(context, callback);
    }

    protected abstract void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Consumer<@NotNull Result> callback
    );

}
