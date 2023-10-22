package w.commander.manual.description;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import w.commander.execution.CommandExecutionContext;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandDescriptionFactory implements CommandDescriptionFactory {

    public static CommandDescriptionFactory create() {
        return new SimpleCommandDescriptionFactory();
    }

    @Override
    public CommandDescription create(String description) {
        return new CommandDescriptionImpl(description);
    }

    @Value
    private static class CommandDescriptionImpl implements CommandDescription {

        String value;

        @Override
        public String format(CommandExecutionContext context) {
            return value;
        }

    }
}
