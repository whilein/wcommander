package w.commander.platform.adventure.execution;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.platform.adventure.AdventureCommandActor;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureExecutionContext extends ExecutionContext {

    @NotNull AdventureCommandActor getActor();

    void dispatch(Component message);

}
