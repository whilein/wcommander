package w.commander.manual.usage;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;

/**
 * @author whilein
 */
public interface Usage {

    @NotNull String format(@NotNull ExecutionContext context);

}
