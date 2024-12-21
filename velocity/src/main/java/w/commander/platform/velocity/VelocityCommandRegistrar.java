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

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.Command;
import w.commander.CommandRegistrar;

import javax.annotation.concurrent.Immutable;

/**
 * @author _Novit_ (novitpw)
 */
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class VelocityCommandRegistrar implements CommandRegistrar {

    CommandManager commandManager;
    VelocityCommanderConfig config;

    protected SimpleCommand create(Command command) {
        return new VelocityCommand(command, config);
    }

    @Override
    public void register(@NotNull Command command) {
        val info = (VelocityCommandInfo) command.getInfo();

        val commandMeta = commandManager.metaBuilder(command.getName())
                .plugin(info.getPlugin())
                .aliases(command.getAliases().toArray(String[]::new))
                .build();

        commandManager.register(commandMeta, create(command));
    }

    @Override
    public void unregister(@NotNull Command command) {
        commandManager.unregister(command.getName());
    }

}
