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

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface Ordered extends Comparable<Ordered> {

    int FIRST = Integer.MIN_VALUE;
    int LAST = Integer.MAX_VALUE;

    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(@NotNull Ordered o) {
        return Integer.compare(getOrder(), o.getOrder());
    }

}
