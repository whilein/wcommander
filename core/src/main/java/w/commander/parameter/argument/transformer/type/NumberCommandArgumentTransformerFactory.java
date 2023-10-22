package w.commander.parameter.argument.transformer.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.error.CommandErrorFactory;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactory;

import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberCommandArgumentTransformerFactory implements CommandArgumentTransformerFactory {

    CommandErrorFactory commandErrorFactory;
    Function<? super String, ? extends Number> fn;

    public static CommandArgumentTransformerFactory create(
            CommandErrorFactory commandErrorFactory,
            Function<? super String, ? extends Number> fn
    ) {
        return new NumberCommandArgumentTransformerFactory(commandErrorFactory, fn);
    }

    @Override
    public CommandArgumentTransformer create(CommandArgument argument) {
        return NumberCommandArgumentTransformer.create(argument, commandErrorFactory, fn);
    }
}