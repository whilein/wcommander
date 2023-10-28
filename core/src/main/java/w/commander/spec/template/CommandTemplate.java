package w.commander.spec.template;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author whilein
 */
public interface CommandTemplate {

    @NotNull Object getInstance();

    @NotNull List<@NotNull CommandTemplate> getSubCommands();

}
