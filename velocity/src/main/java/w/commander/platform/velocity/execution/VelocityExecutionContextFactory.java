package w.commander.platform.velocity.execution;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;
import w.commander.execution.CommandExecutor;
import w.commander.execution.ExecutionContext;
import w.commander.execution.ExecutionContextFactory;
import w.commander.platform.velocity.actor.VelocityCommandActor;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityExecutionContextFactory implements ExecutionContextFactory {

    public static @NotNull ExecutionContextFactory create() {
        return new VelocityExecutionContextFactory();
    }

    @Override
    public @NotNull ExecutionContext create(
            @NotNull CommandActor actor,
            @NotNull CommandExecutor executor,
            @NotNull RawArguments arguments
    ) {
        return VelocityExecutionContext.create(
                (VelocityCommandActor) actor,
                executor,
                arguments
        );
    }
}
