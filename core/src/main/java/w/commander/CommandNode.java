package w.commander;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.CommandExecutor;

/**
 * @author whilein
 */
public interface CommandNode {

    @Nullable CommandNode subCommand(@NotNull String name);

    @NotNull CommandExecutor executor(int arguments);

}
