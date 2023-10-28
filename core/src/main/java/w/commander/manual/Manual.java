package w.commander.manual;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.SuccessResult;

/**
 * @author whilein
 */
public interface Manual {

    @NotNull SuccessResult format(@NotNull ExecutionContext context);

}
