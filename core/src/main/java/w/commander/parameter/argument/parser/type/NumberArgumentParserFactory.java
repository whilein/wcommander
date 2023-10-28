package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberArgumentParserFactory implements ArgumentParserFactory {

    ErrorResultFactory errorResultFactory;
    Function<? super String, ? extends Number> fn;

    public static ArgumentParserFactory create(
            @NotNull ErrorResultFactory errorResultFactory,
            @NotNull Function<? super @NotNull String, ? extends @NotNull Number> fn
    ) {
        return new NumberArgumentParserFactory(errorResultFactory, fn);
    }

    @Override
    public @NotNull ArgumentParser create(@NotNull Argument argument) {
        return NumberArgumentParser.create(argument, errorResultFactory, fn);
    }
}