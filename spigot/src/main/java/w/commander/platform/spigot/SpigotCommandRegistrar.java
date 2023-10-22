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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.Command;
import w.commander.CommandRegistrar;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author _Novit_ (novitpw), whilein
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class SpigotCommandRegistrar implements CommandRegistrar {

    Plugin plugin;
    CommandMap commandMap;

    @Setter
    @NonFinal
    SpigotCommandActorFactory spigotCommandActorFactory;

    public SpigotCommandRegistrar(Plugin plugin) {
        this.plugin = plugin;

        val server = plugin.getServer();

        try {
            val commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access to commandMap");
        }
    }

    protected org.bukkit.command.Command create(Command command) {
        return new SpigotCommand(command, spigotCommandActorFactory);
    }

    @Override
    public void register(@NotNull Command command) {
        commandMap.register(plugin.getName(), create(command));
    }

    @Override
    public void unregister(@NotNull Command command) {
        throw new UnsupportedOperationException(); // TODO
    }

}
