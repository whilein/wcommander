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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.minecraft.MinecraftCommandActor;
import w.commander.platform.adventure.AdventureCommandActor;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * @author whilein
 */
@Immutable
public interface VelocityCommandActor extends MinecraftCommandActor, AdventureCommandActor, ForwardingAudience {

    @NotNull
    @Contract(pure = true)
    CommandSource getSource();

    @Override
    default @NotNull Iterable<? extends Audience> audiences() {
        return List.of(getSource());
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
