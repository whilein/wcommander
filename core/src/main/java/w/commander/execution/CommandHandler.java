package w.commander.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameters;

/**
 * @author whilein
 */
public interface CommandHandler extends CommandExecutor {
    @NotNull String getPath();

    @NotNull HandlerParameters getParameters();

    @NotNull Usage getUsage();
}
