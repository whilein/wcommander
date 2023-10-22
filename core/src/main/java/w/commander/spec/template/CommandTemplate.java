package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandTemplate {

    Object instance;

    List<CommandTemplate> subCommands;

    public static Builder builder(Object instance) {
        return new Builder(instance);
    }

    public static CommandTemplate from(Object instance) {
        return new CommandTemplate(instance, Collections.emptyList());
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        Object instance;

        List<CommandTemplate> subCommands = new ArrayList<>();

        public Builder subCommand(CommandTemplate template) {
            subCommands.add(template);
            return this;
        }

        public Builder subCommand(Object instance) {
            return subCommand(from(instance));
        }

        public Builder subCommand(Object instance, Consumer<Builder> subCommandBuilder) {
            val builder = new Builder(instance);
            subCommandBuilder.accept(builder);
            return subCommand(builder.build());
        }

        public CommandTemplate build() {
            return new CommandTemplate(instance, subCommands);
        }
    }

}
