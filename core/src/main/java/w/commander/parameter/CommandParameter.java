package w.commander.parameter;

import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.cursor.CommandArgumentCursor;

/**
 * @author whilein
 */
public interface CommandParameter {

    Object extract(CommandExecutionContext context, CommandArgumentCursor cursor);

}
