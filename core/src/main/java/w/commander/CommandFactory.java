package w.commander;

import org.jetbrains.annotations.NotNull;
import w.commander.spec.CommandSpec;

/**
 * @author whilein
 */
public interface CommandFactory {

    @NotNull Command create(@NotNull CommandSpec spec);

}
