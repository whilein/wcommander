package w.commander.platform.adventure.execution;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.RawArguments;
import w.commander.execution.AbstractExecutionContext;
import w.commander.execution.CommandExecutor;
import w.commander.platform.adventure.AdventureCommandActor;

/**
 * @author _Novit_ (novitpw)
 */
public abstract class AbstractAdventureExecutionContext<T extends AdventureCommandActor>
        extends AbstractExecutionContext<T>
        implements AdventureExecutionContext {

    protected AbstractAdventureExecutionContext(
            @NotNull T actor,
            @NotNull CommandExecutor executor,
            @NotNull RawArguments rawArguments
    ) {
        super(actor, executor, rawArguments);
    }

    @Override
    public void dispatch(@NotNull Component message) {
        actor.sendMessage(message);
    }

}
