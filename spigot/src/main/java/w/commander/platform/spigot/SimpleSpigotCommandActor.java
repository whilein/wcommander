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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * @author whilein
 */
@Getter
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class SimpleSpigotCommandActor implements SpigotCommandActor {

    CommandSender sender;

    @Override
    @Contract(pure = true)
    public @NotNull String getName() {
        return getSender().getName();
    }

    @Override
    @Contract(pure = true)
    public boolean isPlayer() {
        return getSender() instanceof Player;
    }

    @Override
    @Contract(pure = true)
    public boolean isConsole() {
        return getSender() instanceof ConsoleCommandSender;
    }

    @Override
    public void sendMessage(@NotNull String text) {
        sender.sendMessage(text);
    }
}
