package w.commander.parameter.argument.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.cursor.ArgumentCursor;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdinaryArgument implements Argument {

    String name;
    boolean required;

    @NonFinal
    ArgumentParser parser;

    public static Argument create(
            @NotNull String name,
            boolean required,
            @NotNull ArgumentParserFactory parserFactory
    ) {
        val argument = new OrdinaryArgument(name, required);
        argument.parser = parserFactory.create(argument);

        return argument;
    }

    @Override
    public Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            return parser.parse(context.getRawArguments().value(cursor.next()), context);
        }

        return null;
    }
}
