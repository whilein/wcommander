package w.commander.parameter.argument.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;

/**
 * @author whilein
 */
public interface ArgumentParser {

    @Nullable Object parse(@NotNull String value, @NotNull ExecutionContext context);

}
