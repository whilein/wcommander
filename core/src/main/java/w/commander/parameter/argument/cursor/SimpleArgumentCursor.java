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

package w.commander.parameter.argument.cursor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleArgumentCursor implements ArgumentCursor {

    int optionalRemaining;

    int requiredRemaining;

    int cursor;

    public static @NotNull ArgumentCursor create(
            int argumentCount,
            int requiredArgumentCount
    ) {
        return new SimpleArgumentCursor(
                argumentCount - requiredArgumentCount,
                requiredArgumentCount,
                0
        );
    }

    @Override
    public boolean hasNext(boolean required) {
        if ((required ? requiredRemaining : optionalRemaining) == 0) {
            return false;
        }

        if (required) {
            requiredRemaining--;
        } else {
            optionalRemaining--;
        }

        return true;
    }

    @Override
    public int next() {
        return cursor++;
    }

}
