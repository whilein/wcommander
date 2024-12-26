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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.Command;
import w.commander.CommandRegistrar;

import javax.annotation.concurrent.Immutable;

/**
 * @author _Novit_ (novitpw), whilein
 */
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SpigotCommandRegistrar implements CommandRegistrar {

    CommandMap commandMap;
    SpigotCommanderConfig config;

    public SpigotCommandRegistrar(SpigotCommanderConfig config) {
        this.config = config;

        try {
            val server = Bukkit.getServer();

            val commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access to commandMap");
        }
    }

    protected org.bukkit.command.Command create(Command command) {
        return new SpigotCommand(command, config);
    }

    public @Nullable SpigotCommand getCommand(@NotNull String alias) {
        val command = commandMap.getCommand(alias);
        if (!(command instanceof SpigotCommand)) {
            return null;
        }

        return (SpigotCommand) command;
    }

    @Override
    public void register(@NotNull Command command) {
        val info = (SpigotCommandInfo) command.getInfo();
        commandMap.register(info.getPlugin().getName(), create(command));
    }

    @Override
    public void unregister(@NotNull Command command) {
        throw new UnsupportedOperationException(); // TODO
    }

}
