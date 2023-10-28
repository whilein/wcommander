package w.commander.manual;

import org.jetbrains.annotations.NotNull;
import w.commander.spec.CommandSpec;

/**
 * @author whilein
 */
public interface ManualFactory {

    @NotNull Manual create(@NotNull CommandSpec spec);

}
