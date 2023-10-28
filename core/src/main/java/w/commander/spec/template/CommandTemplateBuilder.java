package w.commander.spec.template;

import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface CommandTemplateBuilder {

    CommandTemplateBuilder subCommand(CommandTemplate template);

    CommandTemplateBuilder subCommand(Object instance);

    CommandTemplateBuilder subCommand(Object instance, Consumer<CommandTemplateBuilder> build);

    CommandTemplate build();

}
