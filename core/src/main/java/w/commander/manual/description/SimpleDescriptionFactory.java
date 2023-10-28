package w.commander.manual.description;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleDescriptionFactory implements DescriptionFactory {

    public static @NotNull DescriptionFactory create() {
        return new SimpleDescriptionFactory();
    }

    @Override
    public @NotNull Description create(@NotNull String description) {
        return new DescriptionImpl(description);
    }

    @Value
    private static class DescriptionImpl implements Description {

        String value;

        @Override
        public @NotNull String format(@NotNull ExecutionContext context) {
            return value;
        }

    }
}
