package w.commander.async;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;

/**
 * @author _Novit_ (novitpw)
 */
public interface ExecutorRegistry {

    void add(@NotNull String name, @NotNull Executor executor);

    @Nullable Executor get(@NotNull String name);

}
