package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.error.CommandErrorFactory;
import w.commander.manual.usage.CommandUsage;
import w.commander.result.CommandResult;

import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotEnoughArgumentsCommandExecutor extends AbstractCommandExecutor {

    CommandUsage commandUsage;
    CommandErrorFactory commandErrorFactory;

    public static CommandExecutor create(
            CommandUsage commandUsage,
            CommandErrorFactory commandErrorFactory
    ) {
        return new NotEnoughArgumentsCommandExecutor(
                commandUsage,
                commandErrorFactory
        );
    }

    @Override
    protected void doExecute(CommandExecutionContext context, Consumer<CommandResult> callback) {
        callback.accept(commandErrorFactory.onNotEnoughArguments(commandUsage));
    }
}
