package w.commander.manual.usage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.CommandArgument;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandUsageFactory implements CommandUsageFactory {

    String prefix;

    public static CommandUsageFactory create(String prefix) {
        return new SimpleCommandUsageFactory(prefix);
    }

    public static CommandUsageFactory create() {
        return create("");
    }

    @Override
    public CommandUsage create(String command, CommandArgument[] arguments) {
        val builder = new StringBuilder(prefix).append(command);

        for (val argument : arguments) {
            builder.append(' ');

            val required = argument.isRequired();

            builder.append(required ? '<' : '(');
            builder.append(argument.getName());
            builder.append(required ? '>' : ')');
        }

        return new CommandUsageImpl(builder.toString());
    }

    @Value
    private static class CommandUsageImpl implements CommandUsage {

        String value;

        @Override
        public String format(CommandExecutionContext context) {
            return value;
        }

    }
}
