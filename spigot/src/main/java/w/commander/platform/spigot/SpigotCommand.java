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

package w.commander.platform.spigot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import w.commander.Command;
import w.commander.RawArguments;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author whilein
 */
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SpigotCommand extends org.bukkit.command.Command {
    Command command;
    SpigotCommanderConfig config;

    public SpigotCommand(Command command, SpigotCommanderConfig config) {
        super(command.getName(), "", "/" + command.getName(), command.getAliases());

        this.command = command;
        this.config = config;
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        val actor = config.getSpigotCommandActorFactory().create(target);

        try {
            val result = command.test(actor)
                    .get(); // надеемся, что Future уже завершен

            return result.isSuccess();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args)
            throws IllegalArgumentException {
        val actor = config.getSpigotCommandActorFactory().create(sender);
        val arguments = RawArguments.fromTrustedArray(args);

        try {
            return command.tabComplete(actor, arguments)
                    .get(); // надеемся, что Future уже завершен
        } catch (InterruptedException | ExecutionException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        val actor = config.getSpigotCommandActorFactory().create(sender);
        val arguments = RawArguments.fromTrustedArray(args);

        command.execute(actor, arguments);
        return true;
    }
}
