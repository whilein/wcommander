package w.commander.parameter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.cursor.ArgumentCursor;

/**
 * @author whilein
 */
public interface HandlerParameter {

    @Nullable Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor);

}
