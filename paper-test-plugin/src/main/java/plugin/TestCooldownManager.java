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

package plugin;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommandActor;
import w.commander.cooldown.CooldownManager;
import w.commander.platform.paper.PaperCommandActor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whilein
 */
public class TestCooldownManager implements CooldownManager {

    Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    @Override
    public @NotNull String getId() {
        return "test";
    }

    @Override
    public boolean hasCooldown(@NotNull CommandActor actor, @NotNull String action) {
        return getCooldown(actor, action) != null;
    }

    @Override
    public @Nullable Duration getCooldown(@NotNull CommandActor actor, @NotNull String action) {
        val actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) return null;

        val pa = (PaperCommandActor) actor;
        val expiresAt = actionCooldowns.get(pa.getName());
        if (expiresAt == null) return null;

        val now = System.currentTimeMillis();
        return expiresAt > now
                ? Duration.ofMillis(expiresAt - now)
                : null;
    }

    @Override
    public void setCooldown(@NotNull CommandActor actor, @NotNull String action, @NotNull Duration duration) {
        val pa = (PaperCommandActor) actor;

        val actionCooldowns = cooldowns.computeIfAbsent(action, __ -> new ConcurrentHashMap<>());
        actionCooldowns.put(pa.getName(), System.currentTimeMillis() + duration.toMillis());
    }

}
