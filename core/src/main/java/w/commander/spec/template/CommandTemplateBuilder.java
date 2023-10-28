package w.commander.spec.template;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface CommandTemplateBuilder {

    @NotNull CommandTemplateBuilder subCommand(@NotNull CommandTemplate template);

    @NotNull CommandTemplateBuilder subCommand(@NotNull Object instance);

    @NotNull CommandTemplateBuilder subCommand(
            @NotNull Object instance,
            @NotNull Consumer<@NotNull CommandTemplateBuilder> build
    );

    @NotNull CommandTemplate build();

}
