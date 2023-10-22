package w.commander.manual;

import w.commander.execution.CommandExecutionContext;
import w.commander.result.SuccessCommandResult;

/**
 * @author whilein
 */
public interface CommandManual {

    SuccessCommandResult format(CommandExecutionContext context);

}
