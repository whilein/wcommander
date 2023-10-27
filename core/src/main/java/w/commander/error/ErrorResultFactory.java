package w.commander.error;

import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.argument.Argument;
import w.commander.result.FailedResult;

import java.util.Map;

/**
 * @author whilein
 */
public interface ErrorResultFactory {

    @NotNull FailedResult onNotEnoughArguments(
            @NotNull Usage usage
    );

    @NotNull FailedResult onInvalidNumber(
            @NotNull Argument argument,
            @NotNull String value
    );

    <E extends Enum<E>> FailedResult onInvalidEnum(
            @NotNull Argument argument,
            @NotNull String value,
            @NotNull Map<@NotNull String, @NotNull E> enumType
    );

}
