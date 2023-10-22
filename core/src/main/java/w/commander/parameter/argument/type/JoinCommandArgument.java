package w.commander.parameter.argument.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.cursor.CommandArgumentCursor;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinCommandArgument implements CommandArgument {

    String name;
    boolean required;
    String delimiter;

    public static CommandArgument create(
            String name,
            boolean required,
            String delimiter
    ) {
        return new JoinCommandArgument(name, required, delimiter);
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object extract(CommandExecutionContext context, CommandArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            return context.rawArguments().join(delimiter, cursor.next());
        }

        return null;
    }
}
