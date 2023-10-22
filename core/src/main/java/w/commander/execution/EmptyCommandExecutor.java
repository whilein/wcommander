package w.commander.execution;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import w.commander.result.CommandResult;
import w.commander.result.CommandResults;

import java.util.function.Consumer;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyCommandExecutor extends AbstractCommandExecutor {

    private static final CommandExecutor INSTANCE = new EmptyCommandExecutor();

    public static CommandExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doExecute(CommandExecutionContext context, Consumer<CommandResult> callback) {
        callback.accept(CommandResults.error());
    }
}
