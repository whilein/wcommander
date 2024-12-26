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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContextFactory;
import w.commander.platform.spigot.SpigotCommandActorFactory;
import w.commander.platform.spigot.SpigotCommandRegistrar;
import w.commander.platform.spigot.SpigotCommanderConfig;

/**
 * @author whilein
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PaperCommanderConfig extends SpigotCommanderConfig {

    public static PaperCommanderConfig createDefaults() {
        val config = new PaperCommanderConfig();
        config.initDefaults();

        return config;
    }

    @Override
    protected void initDefaults() {
        super.initDefaults();

        this.executionContextFactory = new PaperExecutionContextFactory();
        this.spigotCommandActorFactory = SimplePaperCommandActor::new;
    }

    @Override
    public SpigotCommandRegistrar getCommandRegistrar() {
        return (SpigotCommandRegistrar) super.getCommandRegistrar();
    }

    public void setSpigotCommandActorFactory(
            @NotNull PaperCommandActorFactory factory
    ) {
        super.setSpigotCommandActorFactory(factory);
    }

    public void setupAsyncTabCompleteListener(@NotNull Plugin plugin) {
        val listener = new PaperAsyncTabCompleteListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void setSpigotCommandActorFactory(SpigotCommandActorFactory factory) {
        if (!(factory instanceof PaperCommandActorFactory)) {
            throw new UnsupportedOperationException();
        }

        this.setSpigotCommandActorFactory((PaperCommandActorFactory) factory);
    }

    public void setExecutionContextFactory(
            @NotNull PaperExecutionContextFactory factory
    ) {
        super.setExecutionContextFactory(factory);
    }

    @Override
    public void setExecutionContextFactory(ExecutionContextFactory factory) {
        if (!(factory instanceof PaperExecutionContextFactory)) {
            throw new UnsupportedOperationException();
        }

        this.setExecutionContextFactory((PaperExecutionContextFactory) factory);
    }
}
