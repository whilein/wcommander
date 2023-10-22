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

package w.commander.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
@UtilityClass
public class Immutables {

    @SafeVarargs
    public <T> @NotNull List<T> immutableList(T @NotNull ... array) {
        switch (array.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(array[0]);
            default:
                return Collections.unmodifiableList(Arrays.asList(array));
        }
    }

    public <T> @NotNull List<T> immutableList(@NotNull List<? extends T> list) {
        switch (list.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(list.get(0));
            default:
                return Collections.unmodifiableList(new ArrayList<>(list));
        }
    }

    public <K, V> @NotNull Map<K, V> immutableMap(@NotNull Map<? extends K, ? extends V> map) {
        return !map.isEmpty()
                ? Collections.unmodifiableMap(new HashMap<>(map))
                : Collections.emptyMap();
    }

}
