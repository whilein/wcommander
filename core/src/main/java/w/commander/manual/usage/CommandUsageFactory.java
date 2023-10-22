package w.commander.manual.usage;

import w.commander.parameter.argument.CommandArgument;

/**
 * @author whilein
 */
public interface CommandUsageFactory {

    CommandUsage create(String command, CommandArgument[] arguments);

}
