package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;

/**
 * @author whilein
 */
public interface CommandHandler extends CommandExecutor {
    @NotNull String getPath();

    int getArgumentCount();

    int getRequiredArgumentCount();

    @NotNull Usage getUsage();
}
