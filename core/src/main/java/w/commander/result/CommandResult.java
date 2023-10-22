package w.commander.result;

import w.commander.execution.CommandExecutionContext;

/**
 * @author whilein
 */
public interface CommandResult {

    boolean isSuccess();

    void answer(CommandExecutionContext context);

    default CommandResultException asException() {
        return CommandResultException.create(this);
    }
}
