package w.commander;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.CommandExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
public interface CommandNodeFactory {

    @NotNull CommandNode create(
            @NotNull List<@NotNull CommandExecutor> executors,
            @NotNull Map<@NotNull String, @NotNull CommandNode> subCommands
    );

}
