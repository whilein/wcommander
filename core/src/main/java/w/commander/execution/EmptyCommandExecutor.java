package w.commander.execution;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyCommandExecutor extends AbstractCommandExecutor {

    private static final CommandExecutor INSTANCE = new EmptyCommandExecutor();

    public static @NotNull CommandExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        callback.complete(Results.error());
    }
}
