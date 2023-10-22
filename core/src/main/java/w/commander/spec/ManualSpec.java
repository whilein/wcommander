package w.commander.spec;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author whilein
 */
@Value.Immutable
public interface ManualSpec {

    ManualSpec EMPTY = ImmutableManualSpec.builder()
            .hasHandler(false)
            .build();

    boolean hasHandler();

    Optional<String> getSubCommand();

}
