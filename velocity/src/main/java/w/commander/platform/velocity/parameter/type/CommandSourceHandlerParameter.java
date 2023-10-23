package w.commander.platform.velocity.parameter.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.cursor.ArgumentCursor;
import w.commander.platform.velocity.execution.VelocityExecutionContext;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandSourceHandlerParameter implements HandlerParameter {

    private static final HandlerParameter INSTANCE = new CommandSourceHandlerParameter();

    public static @NotNull HandlerParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        return ((VelocityExecutionContext) context).getActor().getSource();
    }

}
