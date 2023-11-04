package w.commander.spec;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author whilein
 */
@Value.Immutable
public interface ManualSpec {

    @NotNull ManualSpec EMPTY = ImmutableManualSpec.builder()
            .hasHandler(false)
            .build();

    boolean hasHandler();

    @Nullable ManualSubCommandSpec getSubCommand();

    default boolean isEmpty() {
        return !hasHandler() && getSubCommand() != null;
    }

}
