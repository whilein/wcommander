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
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import w.commander.CommandInfo;

import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;

/**
 * @author whilein
 */
@Getter
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SpigotCommandInfo extends CommandInfo {
    Plugin plugin;

    public SpigotCommandInfo(Plugin plugin, MethodHandles.Lookup lookup, Object instance) {
        super(instance, lookup);

        this.plugin = plugin;
    }

    public SpigotCommandInfo(Plugin plugin, Object instance) {
        super(instance);

        this.plugin = plugin;
    }
}
