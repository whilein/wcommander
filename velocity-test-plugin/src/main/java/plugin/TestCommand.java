package plugin;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.WithManual;
import w.commander.platform.adventure.result.AdventureResults;
import w.commander.platform.velocity.annotation.PlayerTarget;
import w.commander.result.Result;

/**
 * @author whilein
 */
@WithManual
@Command("test")
public class TestCommand {

    @SubCommandHandler("me")
    public Result me(CommandSource source) {
        return AdventureResults.ok(source.get(Identity.DISPLAY_NAME).orElse(Component.empty()));
    }

    @CommandHandler
    public Result target(@PlayerTarget Player player) {
        return AdventureResults.ok(player.get(Identity.DISPLAY_NAME).orElse(Component.empty()));
    }

}
