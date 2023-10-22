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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

/**
 * @author whilein
 */
public interface Commander {

    @UnmodifiableView @NotNull Collection<? extends @NotNull Command> getCommands();

    @NotNull Command ofGraph(@NotNull CommandGraph graph);

    default @NotNull Command ofInstance(@NotNull Object object) {
        return ofGraph(CommandGraph.of(object));
    }

    void register(@NotNull Command command);

    default void register(@NotNull CommandGraph graph) {
        register(ofGraph(graph));
    }

    default void register(@NotNull Object object) {
        register(ofInstance(object));
    }

    void unregister(@NotNull Class<?> type);

    static @NotNull CommanderBuilder<?> builder() {
        return new CommanderImpl.Builder();
    }

}
