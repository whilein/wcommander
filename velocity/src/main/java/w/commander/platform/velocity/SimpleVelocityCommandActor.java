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

package w.commander.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * @author _Novit_ (novitpw), whilein
 */
@Getter
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class SimpleVelocityCommandActor implements VelocityCommandActor {

    CommandSource source;

    @Override
    @Contract(pure = true)
    public @NotNull String getName() {
        return getSource().get(Identity.NAME).orElse("");
    }

    @Override
    @Contract(pure = true)
    public boolean isPlayer() {
        return getSource() instanceof Player;
    }

    @Override
    @Contract(pure = true)
    public boolean isConsole() {
        return getSource() instanceof ConsoleCommandSource;
    }

}
