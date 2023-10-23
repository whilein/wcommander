package w.commander.platform.velocity.actor;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.platform.adventure.AdventureCommandActor;

/**
 * @author _Novit_ (novitpw)
 */
public interface VelocityCommandActor extends AdventureCommandActor {

    @NotNull CommandSource getSource();

    @Override
    default void sendMessage(@NotNull Component component) {
        getSource().sendMessage(component);
    }

    @Override
    default @NotNull String getName() {
        return getSource().get(Identity.NAME).orElse("");
    }

    default boolean isPlayer() {
        return getSource() instanceof Player;
    }

    default boolean isConsole() {
        return getSource() instanceof ConsoleCommandSource;
    }

    default @Nullable ConsoleCommandSource asConsole() {
        return isConsole() ? (ConsoleCommandSource) getSource() : null;
    }

    default @Nullable Player asPlayer() {
        return isPlayer() ? (Player) getSource() : null;
    }

}
