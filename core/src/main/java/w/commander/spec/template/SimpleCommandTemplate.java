package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;

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

    public static @NotNull CommandTemplate create(
            @NotNull Object instance,
            @NotNull List<@NotNull CommandTemplate> subCommands
    ) {
        return new SimpleCommandTemplate(instance, subCommands);
    }

    public static @NotNull CommandTemplate create(@NotNull Object instance) {
        return create(instance, Collections.emptyList());
    }

    public static @NotNull CommandTemplateBuilder builder(@NotNull Object instance) {
        return new BuilderImpl(instance);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BuilderImpl implements CommandTemplateBuilder {

        Object instance;

        List<CommandTemplate> subCommands = new ArrayList<>();

        @Override
        public CommandTemplateBuilder subCommand(@NotNull CommandTemplate template) {
            subCommands.add(template);
            return this;
        }

        @Override
        public CommandTemplateBuilder subCommand(@NotNull Object instance) {
            return subCommand(create(instance));
        }

        @Override
        public CommandTemplateBuilder subCommand(
                @NotNull Object instance,
                @NotNull Consumer<CommandTemplateBuilder> build
        ) {
            val builder = new BuilderImpl(instance);
            build.accept(builder);
            return subCommand(builder.build());
        }

        @Override
        public @NotNull CommandTemplate build() {
            return SimpleCommandTemplate.create(instance, subCommands);
        }

    }


}
