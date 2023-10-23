package w.commander.platform.velocity.execution;

import org.jetbrains.annotations.NotNull;
import w.commander.RawArguments;
import w.commander.execution.CommandExecutor;
import w.commander.execution.ExecutionContext;
import w.commander.platform.adventure.execution.AbstractAdventureExecutionContext;
import w.commander.platform.velocity.actor.VelocityCommandActor;

/**
 * @author whilein
 */
public final class VelocityExecutionContext extends AbstractAdventureExecutionContext<VelocityCommandActor> {
    private VelocityExecutionContext(
            @NotNull VelocityCommandActor actor,
            @NotNull CommandExecutor executor,
            @NotNull RawArguments rawArguments
    ) {
        super(actor, executor, rawArguments);
    }

    public static @NotNull ExecutionContext create(
            @NotNull VelocityCommandActor actor,
            @NotNull CommandExecutor executor,
            @NotNull RawArguments rawArguments
    ) {
        return new VelocityExecutionContext(actor, executor, rawArguments);
    }
}
