package w.commander.manual;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import w.commander.execution.CommandExecutionContext;
import w.commander.result.CommandResults;
import w.commander.result.SuccessCommandResult;
import w.commander.spec.CommandSpec;
import w.commander.spec.HandlerSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandManualFactory implements CommandManualFactory {

    public static CommandManualFactory create() {
        return new SimpleCommandManualFactory();
    }

    private void listEntries(CommandSpec spec, List<HandlerSpec> result) {
        result.addAll(spec.getHandlers());

        for (val subCommand : spec.getSubCommands()) {
            listEntries(subCommand, result);
        }
    }


    @Override
    public CommandManual create(CommandSpec spec) {
        val name = spec.getName();

        val entries = new ArrayList<HandlerSpec>();
        listEntries(spec, entries);

        entries.sort(Comparator.comparing(HandlerSpec::getPath));

        return new CommandManualImpl(name, entries);
    }

    @Value
    private static class CommandManualImpl implements CommandManual {

        String name;

        List<HandlerSpec> entries;

        private void append(HandlerSpec spec, CommandExecutionContext context, StringBuilder out) {
            out.append('\n')
                    .append(spec.getUsage().format(context))
                    .append(" - ").append(spec.getDescription().format(context));
        }

        @Override
        public SuccessCommandResult format(CommandExecutionContext context) {
            val builder = new StringBuilder("Command ").append(name).append(':');

            for (val entry : entries) {
                append(entry, context, builder);
            }

            return CommandResults.ok(builder.toString());
        }

    }
}
