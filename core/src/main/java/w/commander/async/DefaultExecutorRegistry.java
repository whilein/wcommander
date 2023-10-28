package w.commander.async;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultExecutorRegistry implements ExecutorRegistry {

    Map<String, Executor> executorMap;

    public static ExecutorRegistry create() {
        return create(new ConcurrentHashMap<>());
    }

    public static ExecutorRegistry create(Map<String, Executor> map) {
        return new DefaultExecutorRegistry(map);
    }

    @Override
    public void add(@NotNull String name, @NotNull Executor executor) {
        executorMap.put(name.toLowerCase(), executor);
    }

    @Override
    public @Nullable Executor get(@NotNull String name) {
        return executorMap.get(name.toLowerCase());
    }
}
