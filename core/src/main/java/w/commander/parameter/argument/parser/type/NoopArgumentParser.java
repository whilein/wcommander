package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.parser.ArgumentParser;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopArgumentParser implements ArgumentParser {

    private static final ArgumentParser INSTANCE = new NoopArgumentParser();

    public static @NotNull ArgumentParser getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable Object parse(@NotNull String value, @NotNull ExecutionContext context) {
        return value;
    }
}
