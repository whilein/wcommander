/*
 *    Copyright 2024 Whilein
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

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import w.commander.annotation.Arg;
import w.commander.annotation.Async;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Cooldown;
import w.commander.annotation.NonRequired;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.TabComplete;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.cooldown.CooldownResult;
import w.commander.minecraft.annotation.NonSelfPlayer;
import w.commander.parameter.argument.validator.type.Between;
import w.commander.parameter.argument.validator.type.Regex;
import w.commander.platform.spigot.annotation.PlayerTarget;
import w.commander.platform.spigot.annotation.WorldTarget;
import w.commander.result.Result;
import w.commander.result.Results;

import java.time.Duration;

/**
 * @author whilein
 */
@WithManual
@Command("test")
public class TestCommand {

    @CommandHandler
    public Result args1(@TabComplete({"foo", "bar", "baz"}) @Arg String arg0) {
        return Results.ok(arg0);
    }

    @CommandHandler
    public Result args2(
            @TabComplete({"foo", "bar", "baz"}) @Arg String arg0,
            @TabComplete({"aaa", "bbb", "ccc"}) @Arg String arg1
    ) {
        return Results.ok(arg0 + " " + arg1);
    }

    @Permission("wcommander")
    @SubCommandHandler("perm")
    public Result perm(
            @TabComplete({"foo", "bar", "baz"}) @Arg String arg0,
            @TabComplete({"aaa", "bbb", "ccc"}) @Arg String arg1
    ) {
        return args2(arg0, arg1);
    }

    @SubCommandHandler("cooldown")
    @Cooldown(cooldownManager = "test")
    @WithDescription("cooldown")
    public Result cooldown(CommandSender sender) {
        return CooldownResult.of(Results.ok("cooldown set"), Duration.ofSeconds(5));
    }

    @SubCommandHandler("player")
    @WithDescription("do magic with player")
    public Result player(CommandSender sender, @NonSelfPlayer @TabComplete(use = "PLAYER") @PlayerTarget("player") Player player) {
        return Results.ok(player.getName() + ": " + player.getHealth());
    }

    @SubCommandHandler("world")
    @WithDescription("do magic with world")
    public Result world(CommandSender sender, @TabComplete(use = "WORLD") @WorldTarget("world") World world) {
        return Results.ok(world.getName() + ": " + world.getEnvironment());
    }

    @Async
    @SubCommandHandler("async")
    @WithDescription("do magic async")
    public Result async(CommandSender sender) {
        return Results.ok(Thread.currentThread().getName());
    }

    @Async
    @SubCommandHandler("valid-async")
    public Result validAsync(CommandSender sender, @Arg @Between(min = 1, max = 100) double v) {
        return Results.ok(String.valueOf(v));
    }

    @SubCommandHandler("valid")
    public Result valid(CommandSender sender, @Arg @Between(min = 1, max = 100) double v) {
        return Results.ok(String.valueOf(v));
    }

    @SubCommandHandler("valid-non-required")
    public Result validNonRequired(CommandSender sender, @NonRequired @Arg @Between(min = 1, max = 100) Double v) {
        return Results.ok(String.valueOf(v));
    }

    @SubCommandHandler("valid-regexp")
    public Result validRegex(CommandSender sender, @Arg @Regex("hello *,? *world(?i)") String value) {
        return Results.ok(value);
    }
}
