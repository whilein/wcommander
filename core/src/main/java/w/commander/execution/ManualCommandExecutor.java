package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.manual.Manual;
import w.commander.result.Result;

import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualCommandExecutor extends AbstractCommandExecutor {

    @NotNull Manual manual;

    public static @NotNull CommandExecutor create(@NotNull Manual manual) {
        return new ManualCommandExecutor(manual);
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Consumer<@NotNull Result> callback
    ) {
        callback.accept(manual.format(context));
    }

    @Override
    public boolean isOverrideable() {
        return true;
    }

}
