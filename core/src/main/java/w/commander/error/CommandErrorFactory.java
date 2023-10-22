package w.commander.error;

import w.commander.manual.usage.CommandUsage;
import w.commander.parameter.argument.CommandArgument;
import w.commander.result.FailedCommandResult;

/**
 * @author whilein
 */
public interface CommandErrorFactory {

    FailedCommandResult onNotEnoughArguments(CommandUsage usage);

    FailedCommandResult onInvalidNumber(CommandArgument argument, String value);

    <E extends Enum<E>> FailedCommandResult onInvalidEnum(CommandArgument argument, String value, Class<E> enumType);

}
