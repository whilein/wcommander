package w.commander.parameter.argument.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.cursor.ArgumentCursor;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinArgument implements Argument {

    String name;
    boolean required;
    String delimiter;

    public static @NotNull Argument create(
            @NotNull String name,
            boolean required,
            @NotNull String delimiter
    ) {
        return new JoinArgument(name, required, delimiter);
    }

    @Override
    public int getMaxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            return context.getRawArguments().join(delimiter, cursor.next());
        }

        return null;
    }
}
