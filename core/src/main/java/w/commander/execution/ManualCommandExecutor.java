package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.manual.Manual;
import w.commander.result.Result;
import w.commander.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManualCommandExecutor extends AbstractCommandExecutor {

    Manual manual;

    public static @NotNull CommandExecutor create(@NotNull Manual manual) {
        return new ManualCommandExecutor(manual);
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        callback.complete(manual.format(context));
    }

    @Override
    public boolean isYielding() {
        return true;
    }

}
