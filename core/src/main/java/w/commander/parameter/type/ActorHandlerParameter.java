package w.commander.parameter.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.cursor.ArgumentCursor;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActorHandlerParameter implements HandlerParameter {

    private static final HandlerParameter INSTANCE = new ActorHandlerParameter();

    public static @NotNull HandlerParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        return context.getActor();
    }
}
