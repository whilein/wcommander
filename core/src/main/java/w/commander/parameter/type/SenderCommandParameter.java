package w.commander.parameter.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.CommandParameter;
import w.commander.parameter.argument.cursor.CommandArgumentCursor;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SenderCommandParameter implements CommandParameter {

    private static final CommandParameter INSTANCE = new SenderCommandParameter();

    public static CommandParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public Object extract(CommandExecutionContext context, CommandArgumentCursor cursor) {
        return context.sender();
    }
}
