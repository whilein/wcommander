package w.commander.result;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;

/**
 * @author whilein
 */
public interface Result {

    boolean isSuccess();

    void answer(@NotNull ExecutionContext context);

    default @NotNull ResultException asException() {
        return ResultException.create(this);
    }
}
