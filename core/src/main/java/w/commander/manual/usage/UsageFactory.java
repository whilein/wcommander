package w.commander.manual.usage;

import org.jetbrains.annotations.NotNull;
import w.commander.parameter.argument.Argument;

import java.util.List;

/**
 * @author whilein
 */
public interface UsageFactory {

    @NotNull Usage create(@NotNull String command, @NotNull List<? extends @NotNull Argument> arguments);

}
