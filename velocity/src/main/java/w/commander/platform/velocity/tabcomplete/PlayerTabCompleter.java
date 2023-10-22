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

package w.commander.platform.velocity.tabcomplete;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.tabcomplete.NamedTabCompleter;
import w.commander.tabcomplete.Suggestions;
import w.commander.util.StringUtils;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class PlayerTabCompleter implements NamedTabCompleter {

    ProxyServer server;

    @Override
    public @NotNull String getName() {
        return "PLAYER";
    }

    @Override
    public void getSuggestions(@NotNull ExecutionContext ctx, @NotNull String value, @NotNull Suggestions suggestions) {
        for (val player : server.getAllPlayers()) {
            val name = player.getUsername();
            if (StringUtils.startsWithIgnoreCase(name, value)) {
                suggestions.next(name);
            }
        }
    }
}
