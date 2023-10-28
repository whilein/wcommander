package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;

/**
 * @author whilein
 */
public interface ExecutionThrowableInterceptor {

    @NotNull Result intercept(@NotNull Throwable throwable);

}
