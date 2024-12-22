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

package w.commander.platform.paper;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.platform.adventure.AdventureCommandActor;
import w.commander.platform.spigot.SpigotCommandActor;

/**
 * @author whilein
 */
public interface PaperCommandActor extends SpigotCommandActor, AdventureCommandActor {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        getSender().sendMessage(source, message, type);
    }
}
