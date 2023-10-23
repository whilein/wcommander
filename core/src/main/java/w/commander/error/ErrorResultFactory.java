package w.commander.error;

import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.argument.Argument;
import w.commander.result.FailedResult;
import w.commander.result.Results;

import java.util.Map;

/**
 * @author whilein
 */
public interface ErrorResultFactory {

    default @NotNull FailedResult onInternalError(@NotNull Throwable throwable) {
        return Results.error();
    }

    default @NotNull FailedResult onNotEnoughArguments(@NotNull Usage usage) {
        return Results.error();
    }

    default @NotNull FailedResult onInvalidNumber(@NotNull Argument argument, @NotNull String value) {
        return Results.error();
    }

    default <E extends Enum<E>> FailedResult onInvalidEnum(
            @NotNull Argument argument,
            @NotNull String value,
            @NotNull Map<@NotNull String, @NotNull E> enumType
    ) {
        return Results.error();
    }

}
