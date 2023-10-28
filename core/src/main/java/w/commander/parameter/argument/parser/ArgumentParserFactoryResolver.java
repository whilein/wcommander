package w.commander.parameter.argument.parser;

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface ArgumentParserFactoryResolver {

    boolean isSupported(@NotNull Class<?> type);

    @NotNull ArgumentParserFactory resolve(@NotNull Class<?> type);

}
