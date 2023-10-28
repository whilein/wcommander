package w.commander.parameter.argument.parser;

import org.jetbrains.annotations.NotNull;
import w.commander.parameter.argument.Argument;

/**
 * @author whilein
 */
public interface ArgumentParserFactory {

    @NotNull ArgumentParser create(@NotNull Argument argument);

}
