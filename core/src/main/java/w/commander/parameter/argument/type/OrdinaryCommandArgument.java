package w.commander.parameter.argument.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.cursor.CommandArgumentCursor;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactory;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdinaryCommandArgument implements CommandArgument {

    String name;
    boolean required;

    @NonFinal
    CommandArgumentTransformer argumentTransformer;

    public static CommandArgument create(
            String name,
            boolean required,
            CommandArgumentTransformerFactory argumentTransformerFactory
    ) {
        val argument = new OrdinaryCommandArgument(name, required);
        argument.argumentTransformer = argumentTransformerFactory.create(argument);

        return argument;
    }

    @Override
    public Object extract(CommandExecutionContext context, CommandArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            return argumentTransformer.transform(context.rawArguments().value(cursor.next()), context);
        }

        return null;
    }
}
