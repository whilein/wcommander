package w.commander.execution;

import w.commander.result.CommandResult;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public abstract class AbstractCommandExecutor implements CommandExecutor {

    public final void execute(CommandExecutionContext context, Consumer<CommandResult> callback) {
        // todo conditions

        doExecute(context, callback);
    }

    protected abstract void doExecute(CommandExecutionContext context, Consumer<CommandResult> callback);

}
