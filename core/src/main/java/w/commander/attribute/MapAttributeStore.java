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

package w.commander.attribute;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MapAttributeStore implements AttributeStore {

    Map<Class<?>, Object> map;

    public MapAttributeStore() {
        this(new HashMap<>());
    }

    @Override
    public <T> void setAttribute(@NotNull Class<T> type, @Nullable T value) {
        map.put(type, value);
    }

    @Override
    public <T> T getAttribute(@NotNull Class<T> type) {
        return type.cast(map.get(type));
    }

    @Override
    public boolean isAttributeSet(@NotNull Class<?> type) {
        return map.containsKey(type);
    }
}
