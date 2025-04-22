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

package w.commander.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.minecraft.MinecraftCommandActor;
import w.commander.platform.adventure.AdventureCommandActor;

import javax.annotation.concurrent.Immutable;

/**
 * @author whilein
 */
@Immutable
public interface VelocityCommandActor extends MinecraftCommandActor, AdventureCommandActor {

    @NotNull
    @Contract(pure = true)
    CommandSource getSource();

    @Override
    @SuppressWarnings("UnstableApiUsage")
    default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        getSource().sendMessage(source, message, type);
    }

    @Override
    @Contract(pure = true)
    default @NotNull CommandSource getIdentity() {
        return getSource();
    }

    @Contract(pure = true)
    default @Nullable ConsoleCommandSource asConsole() {
        return isConsole() ? (ConsoleCommandSource) getSource() : null;
    }

    @Contract(pure = true)
    default @Nullable Player asPlayer() {
        return isPlayer() ? (Player) getSource() : null;
    }

}
