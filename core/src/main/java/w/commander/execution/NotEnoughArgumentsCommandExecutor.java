package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.manual.usage.Usage;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotEnoughArgumentsCommandExecutor extends AbstractCommandExecutor {

    Usage usage;
    ErrorResultFactory errorResultFactory;

    public static @NotNull CommandExecutor create(
            @NotNull Usage usage,
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return new NotEnoughArgumentsCommandExecutor(
                usage,
                errorResultFactory
        );
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        callback.complete(errorResultFactory.onNotEnoughArguments(usage));
    }
}
