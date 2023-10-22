package w.commander.execution;

import w.commander.result.CommandResult;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface CommandExecutor {

    void execute(CommandExecutionContext context, Consumer<CommandResult> callback);

    default boolean isOverrideable() {
        return false;
    }

}
