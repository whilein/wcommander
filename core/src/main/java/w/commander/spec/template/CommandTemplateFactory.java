package w.commander.spec.template;

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface CommandTemplateFactory {

    @NotNull CommandTemplate create(@NotNull Class<?> type);

    @NotNull CommandTemplate create(@NotNull Object instance);

}
