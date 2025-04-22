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

package w.commander.platform.spigot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import w.commander.Commander;
import w.commander.CommanderConfig;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpigotCommander extends Commander {

    @Delegate(types = SpigotCommanderConfig.class, excludes = CommanderConfig.class)
    SpigotCommanderConfig spigotConfig;

    public SpigotCommander(SpigotCommanderConfig config) {
        super(config);

        this.spigotConfig = config;
    }

    public SpigotCommander() {
        this(SpigotCommanderConfig.createDefaults());
    }

    public void register(@NotNull Plugin plugin, @NotNull Object instance) {
        super.register(new SpigotCommandInfo(plugin, instance));
    }

    public void register(@NotNull Object instance) {
        register(JavaPlugin.getProvidingPlugin(instance.getClass()), instance);
    }

}
