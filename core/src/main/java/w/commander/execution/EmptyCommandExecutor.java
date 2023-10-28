package w.commander.execution;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;
import w.commander.result.Results;

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
            @NotNull Consumer<@NotNull Result> callback
    ) {
        callback.accept(Results.error());
    }
}
