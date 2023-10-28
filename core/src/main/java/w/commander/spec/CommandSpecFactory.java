package w.commander.spec;

import org.jetbrains.annotations.NotNull;
import w.commander.spec.template.CommandTemplate;
import w.commander.spec.template.SimpleCommandTemplate;

/**
 * @author whilein
 */
public interface CommandSpecFactory {

    @NotNull CommandSpec create(@NotNull CommandTemplate template);

    default @NotNull CommandSpec create(@NotNull Object instance) {
        return create(SimpleCommandTemplate.create(instance));
    }

}
