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

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.minecraft.MinecraftCommandActor;

/**
 * @author whilein
 */
public interface SpigotCommandActor extends MinecraftCommandActor {

    @NotNull
    String getName();

    @NotNull
    CommandSender getSender();

    @Override
    default @NotNull CommandSender getIdentity() {
        return getSender();
    }

    default @Nullable ConsoleCommandSender asConsole() {
        return isConsole() ? (ConsoleCommandSender) getSender() : null;
    }

    default @Nullable Player asPlayer() {
        return isPlayer() ? (Player) getSender() : null;
    }

}