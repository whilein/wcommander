package w.commander.parameter.argument.transformer;

import w.commander.execution.CommandExecutionContext;

/**
 * @author whilein
 */
public interface CommandArgumentTransformer {

    Object transform(String value, CommandExecutionContext context);

}
