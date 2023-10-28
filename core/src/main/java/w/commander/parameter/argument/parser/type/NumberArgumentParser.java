package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;

import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberArgumentParser implements ArgumentParser {

    Argument argument;
    ErrorResultFactory errorResultFactory;
    Function<? super String, ? extends Number> fn;

    public static @NotNull ArgumentParser create(
            @NotNull Argument argument,
            @NotNull ErrorResultFactory errorResultFactory,
            @NotNull Function<? super @NotNull String, ? extends @NotNull Number> fn
    ) {
        return new NumberArgumentParser(argument, errorResultFactory, fn);
    }

    @Override
    public @NotNull Object parse(@NotNull String value, @NotNull ExecutionContext context) {
        try {
            return fn.apply(value);
        } catch (NumberFormatException e) {
            return errorResultFactory.onInvalidNumber(argument, value);
        }
    }
}
