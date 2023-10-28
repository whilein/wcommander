package w.commander;

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface CommandActor {

    @NotNull String getName();

    void sendMessage(@NotNull String text);

}
