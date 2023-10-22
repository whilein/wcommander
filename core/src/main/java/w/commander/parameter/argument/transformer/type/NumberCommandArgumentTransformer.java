package w.commander.parameter.argument.transformer.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.error.CommandErrorFactory;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;

import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberCommandArgumentTransformer implements CommandArgumentTransformer {

    CommandArgument argument;
    CommandErrorFactory commandErrorFactory;
    Function<? super String, ? extends Number> fn;

    public static CommandArgumentTransformer create(
            CommandArgument argument,
            CommandErrorFactory commandErrorFactory,
            Function<? super String, ? extends Number> fn
    ) {
        return new NumberCommandArgumentTransformer(argument, commandErrorFactory, fn);
    }

    @Override
    public Object transform(String value, CommandExecutionContext context) {
        try {
            return fn.apply(value);
        } catch (NumberFormatException e) {
            return commandErrorFactory.onInvalidNumber(argument, value);
        }
    }
}
