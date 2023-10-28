package w.commander.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.argument.Argument;
import w.commander.result.Results;
import w.commander.result.FailedResult;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopErrorResultFactory implements ErrorResultFactory {

    public static @NotNull ErrorResultFactory create() {
        return new NoopErrorResultFactory();
    }

    @Override
    public @NotNull FailedResult onNotEnoughArguments(@NotNull Usage usage) {
        return Results.error();
    }

    @Override
    public @NotNull FailedResult onInvalidNumber(@NotNull Argument argument, @NotNull String value) {
        return Results.error();
    }

    @Override
    public <E extends Enum<E>> @NotNull FailedResult onInvalidEnum(
            @NotNull Argument argument,
            @NotNull String value,
            @NotNull Class<E> enumType
    ) {
        return Results.error();
    }
}
