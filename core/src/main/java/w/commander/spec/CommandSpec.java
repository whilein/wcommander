package w.commander.spec;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
@Value.Immutable
public interface CommandSpec {

    Object instance();

    Class<?> type();

    String name();

    String path();

    String[] aliases();

    ManualSpec manual();

    List<HandlerSpec> handlers();

    List<CommandSpec> subCommands();

}
