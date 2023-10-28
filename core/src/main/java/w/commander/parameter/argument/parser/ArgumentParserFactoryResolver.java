package w.commander.parameter.argument.parser;

import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.parser.type.NumberArgumentParserFactoryResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
public interface ArgumentParserFactoryResolver {

    static @NotNull List<? extends @NotNull ArgumentParserFactoryResolver> listDefaults(
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return Collections.unmodifiableList(Arrays.asList(
                NumberArgumentParserFactoryResolver.create(errorResultFactory)
        ));
    }

    boolean isSupported(@NotNull Class<?> type);

    @NotNull ArgumentParserFactory resolve(@NotNull Class<?> type);

}
