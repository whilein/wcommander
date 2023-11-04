package w.commander.spec;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author whilein
 */
@Value.Immutable
public interface CommandSpec extends NameAwareSpec, PathAwareSpec {

    @NotNull Object getInstance();

    @NotNull Class<?> getType();

    @NotNull ManualSpec getManual();

    @NotNull List<@NotNull HandlerSpec> getHandlers();

    @NotNull List<@NotNull CommandSpec> getSubCommands();

}
