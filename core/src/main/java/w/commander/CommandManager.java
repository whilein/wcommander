package w.commander;

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface CommandManager {

    void register(@NotNull Object instance);

    void register(@NotNull Class<?> type);

    void unregister(@NotNull Object instance);

    void unregister(@NotNull Class<?> type);

}
