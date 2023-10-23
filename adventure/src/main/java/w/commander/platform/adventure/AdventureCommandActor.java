package w.commander.platform.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureCommandActor extends CommandActor, Audience {

    @Override
    default void sendMessage(String text) {
        sendMessage(Component.text(text));
    }

    void sendMessage(@NotNull Component component);

}
