package w.commander.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import w.commander.manual.usage.CommandUsage;
import w.commander.parameter.argument.CommandArgument;
import w.commander.result.CommandResults;
import w.commander.result.FailedCommandResult;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopCommandErrorFactory implements CommandErrorFactory {

    public static CommandErrorFactory create() {
        return new NoopCommandErrorFactory();
    }

    @Override
    public FailedCommandResult onNotEnoughArguments(CommandUsage usage) {
        return CommandResults.error();
    }

    @Override
    public FailedCommandResult onInvalidNumber(CommandArgument argument, String value) {
        return CommandResults.error();
    }

    @Override
    public <E extends Enum<E>> FailedCommandResult onInvalidEnum(
            CommandArgument argument,
            String value,
            Class<E> enumType
    ) {
        return CommandResults.error();
    }
}
