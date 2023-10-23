package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExecutionContext<T extends CommandActor> implements ExecutionContext {

    @NotNull T actor;
    @NotNull CommandExecutor executor;
    @NotNull RawArguments rawArguments;

    @Override
    public void dispatch(@NotNull String text) {
        actor.sendMessage(text);
    }
}
