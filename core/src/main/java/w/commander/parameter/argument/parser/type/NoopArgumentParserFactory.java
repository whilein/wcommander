package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopArgumentParserFactory implements ArgumentParserFactory {

    private static final ArgumentParserFactory INSTANCE = new NoopArgumentParserFactory();

    public static @NotNull ArgumentParserFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull ArgumentParser create(@NotNull Argument argument) {
        return NoopArgumentParser.getInstance();
    }
}
