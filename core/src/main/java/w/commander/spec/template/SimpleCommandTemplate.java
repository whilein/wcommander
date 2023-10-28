package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCommandTemplate implements CommandTemplate {

    Object instance;

    List<CommandTemplate> subCommands;

    public static CommandTemplate create(Object instance, List<CommandTemplate> subCommands) {
        return new SimpleCommandTemplate(instance, subCommands);
    }

    public static CommandTemplate create(Object instance) {
        return create(instance, Collections.emptyList());
    }

    public static CommandTemplateBuilder builder(Object instance) {
        return new BuilderImpl(instance);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BuilderImpl implements CommandTemplateBuilder {

        Object instance;

        List<CommandTemplate> subCommands = new ArrayList<>();

        @Override
        public CommandTemplateBuilder subCommand(CommandTemplate template) {
            subCommands.add(template);
            return this;
        }

        @Override
        public CommandTemplateBuilder subCommand(Object instance) {
            return subCommand(create(instance));
        }

        @Override
        public CommandTemplateBuilder subCommand(Object instance, Consumer<CommandTemplateBuilder> build) {
            val builder = new BuilderImpl(instance);
            build.accept(builder);
            return subCommand(builder.build());
        }

        @Override
        public CommandTemplate build() {
            return SimpleCommandTemplate.create(instance, subCommands);
        }

    }


}
