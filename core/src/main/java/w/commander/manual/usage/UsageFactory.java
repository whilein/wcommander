package w.commander.manual.usage;

import org.jetbrains.annotations.NotNull;
import w.commander.parameter.argument.Argument;

/**
 * @author whilein
 */
public interface UsageFactory {

    @NotNull Usage create(@NotNull String command, @NotNull Argument @NotNull [] arguments);

}
