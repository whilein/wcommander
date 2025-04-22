/*
 *    Copyright 2025 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package plugin;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.platform.adventure.AdventureResults;
import w.commander.platform.velocity.annotation.PlayerTarget;
import w.commander.result.Result;

/**
 * @author whilein
 */
@WithManual
@Command("test")
public class TestCommand {

    @CommandHandler
    @WithDescription("do magic")
    public Result test(CommandSource source, @PlayerTarget("player") Player player) {
        return AdventureResults.ok(player.get(Identity.DISPLAY_NAME).orElse(Component.empty()));
    }

}
