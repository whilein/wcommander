package w.commander.manual.usage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleUsageFactory implements UsageFactory {

    String prefix;

    public static @NotNull UsageFactory create(@NotNull String prefix) {
        return new SimpleUsageFactory(prefix);
    }

    public static @NotNull UsageFactory create() {
        return create("");
    }

    @Override
    public @NotNull Usage create(@NotNull String command, @NotNull Argument @NotNull [] arguments) {
        val builder = new StringBuilder(prefix).append(command);

        for (val argument : arguments) {
            builder.append(' ');

            val required = argument.isRequired();

            builder.append(required ? '<' : '(');
            builder.append(argument.getName());
            builder.append(required ? '>' : ')');
        }

        return new UsageImpl(builder.toString());
    }

    @Value
    private static class UsageImpl implements Usage {

        String value;

        @Override
        public @NotNull String format(@NotNull ExecutionContext context) {
            return value;
        }

    }
}
