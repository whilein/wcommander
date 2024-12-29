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

package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import w.commander.spec.CommandSpecFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Commander {

    Map<Class<?>, Command> type2CommandMap = new ConcurrentHashMap<>();

    @Getter
    @UnmodifiableView
    Collection<? extends Command> commands = Collections.unmodifiableCollection(type2CommandMap.values());

    @Getter
    @Delegate(types = CommanderConfig.class)
    CommanderConfig config;

    CommandSpecFactory commandSpecFactory;
    CommandFactory commandFactory;

    public Commander() {
        this(CommanderConfig.createDefaults());
    }

    public Commander(CommanderConfig config) {
        this.config = config;
        this.commandSpecFactory = new CommandSpecFactory(config);
        this.commandFactory = new CommandFactory(config);
    }

    public @NotNull Command createFromGraph(@NotNull CommandGraph graph) {
        return commandFactory.create(commandSpecFactory.create(graph));
    }

    public @NotNull Command createFromInfo(@NotNull CommandInfo info) {
        return createFromGraph(CommandGraph.of(info));
    }

    public @NotNull Command register(@NotNull CommandGraph graph) {
        val command = createFromGraph(graph);
        register(command);
        return command;
    }

    public @NotNull Command register(@NotNull CommandInfo info) {
        val command = createFromInfo(info);
        register(command);
        return command;
    }

    public void register(@NotNull Command command) {
        val commandType = command.getInfo().getInstanceType();

        val prevCommand = type2CommandMap.put(commandType, command);
        if (prevCommand != null) {
            config.getCommandRegistrar().unregister(prevCommand);
        }
        config.getCommandRegistrar().register(command);
    }

    public void unregister(@NotNull Class<?> type) {
        val command = type2CommandMap.remove(type);
        if (command == null) return;

        config.getCommandRegistrar().unregister(command);
    }

}
