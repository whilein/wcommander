package w.commander.manual;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.Results;
import w.commander.result.SuccessResult;
import w.commander.spec.CommandSpec;
import w.commander.spec.HandlerSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleManualFactory implements ManualFactory {

    public static @NotNull ManualFactory create() {
        return new SimpleManualFactory();
    }

    private void listEntries(CommandSpec spec, List<HandlerSpec> result) {
        result.addAll(spec.getHandlers());

        for (val subCommand : spec.getSubCommands()) {
            listEntries(subCommand, result);
        }
    }

    @Override
    public @NotNull Manual create(@NotNull CommandSpec spec) {
        val name = spec.getName();

        val entries = new ArrayList<HandlerSpec>();
        listEntries(spec, entries);

        entries.sort(Comparator.comparing(HandlerSpec::getPath));

        return new ManualImpl(name, entries);
    }

    @Value
    private static class ManualImpl implements Manual {

        String name;

        List<HandlerSpec> entries;

        private void append(HandlerSpec spec, ExecutionContext context, StringBuilder out) {
            out.append('\n')
                    .append(spec.getUsage().format(context))
                    .append(" - ").append(spec.getDescription().format(context));
        }

        @Override
        public @NotNull SuccessResult format(@NotNull ExecutionContext context) {
            val builder = new StringBuilder("Command ").append(name).append(':');

            for (val entry : entries) {
                append(entry, context, builder);
            }

            return Results.ok(builder.toString());
        }

    }
}
