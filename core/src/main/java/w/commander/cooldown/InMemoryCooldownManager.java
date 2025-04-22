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

package w.commander.cooldown;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommandActor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class InMemoryCooldownManager implements CooldownManager {

    @Getter
    String id;

    Map<String, Expirations> map;

    @NonFinal
    long minExpiresAt;

    public InMemoryCooldownManager(String id, Map<String, Expirations> map) {
        this.id = id;
        this.map = map;
    }

    public InMemoryCooldownManager(String id) {
        this(id, new HashMap<>());
    }

    private void cleanup0(long time) {
        map.values().removeIf(ue -> ue.cleanup(time));
    }

    @Override
    public boolean hasCooldown(@NotNull CommandActor actor, @NotNull String action) {
        val user2Cooldown = map.get(action);

        return user2Cooldown != null && user2Cooldown.contains(actor.getName());
    }

    @Override
    public @Nullable Duration getCooldown(@NotNull CommandActor actor, @NotNull String action) {
        val user2Cooldown = map.get(action);

        long expiresAt;
        if (user2Cooldown != null && (expiresAt = user2Cooldown.get(actor.getName())) != 0) {
            val remainingMs = expiresAt - System.currentTimeMillis();

            if (remainingMs > 0) {
                return Duration.ofMillis(remainingMs);
            }
        }

        return null;
    }

    @Override
    public void setCooldown(@NotNull CommandActor actor, @NotNull String action, @NotNull Duration duration) {
        if (duration.isNegative()) {
            return;
        }

        val time = System.currentTimeMillis();
        val expiresAt = time + duration.toMillis();

        val user2Cooldown = map.computeIfAbsent(action, __ -> new Expirations());
        user2Cooldown.put(actor.getName(), expiresAt);

        long minExpiresAt;
        if ((minExpiresAt = this.minExpiresAt) == 0 || minExpiresAt > expiresAt) {
            this.minExpiresAt = expiresAt;
        } else if (minExpiresAt < time) {
            cleanup0(time);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class Expirations {

        Map<String, Long> map = new HashMap<>();

        @NonFinal
        long minExpiresAt;

        public boolean contains(String name) {
            return get(name) > System.currentTimeMillis();
        }

        public long get(String name) {
            return map.getOrDefault(name.toLowerCase(), 0L);
        }

        public void put(String name, long expiresAt) {
            map.put(name.toLowerCase(), expiresAt);

            long minExpiresAt;
            if ((minExpiresAt = this.minExpiresAt) == 0 || minExpiresAt > expiresAt) {
                this.minExpiresAt = expiresAt;
            }
        }

        public boolean cleanup(long time) {
            if (minExpiresAt > time) return false;

            map.values().removeIf(expiresAt -> time > expiresAt);

            return map.isEmpty();
        }
    }

}
