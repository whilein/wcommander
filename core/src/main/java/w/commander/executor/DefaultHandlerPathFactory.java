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

package w.commander.executor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public enum DefaultHandlerPathFactory implements HandlerPathFactory {

    LOWER_SNAKE_CASE {
        @Override
        public @NotNull HandlerPath create(@NotNull String name) {
            return new LowerSnakeCasePath(name);
        }
    },

    UPPER_SNAKE_CASE {
        @Override
        public @NotNull HandlerPath create(@NotNull String name) {
            return new UpperSnakeCasePath(name);
        }
    };

    @Getter
    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static abstract class SnakeCasePath implements HandlerPath {
        SnakeCasePath parent;
        String name;

        @Override
        public String toString() {
            val sb = new StringBuilder();
            write(sb);
            return sb.toString();
        }

        private void write(StringBuilder sb) {
            if (parent != null) {
                parent.write(sb);
                sb.append('_');
            }

            sb.append(name);
        }

    }

    private static final class UpperSnakeCasePath extends SnakeCasePath {

        public UpperSnakeCasePath(SnakeCasePath parent, String name) {
            super(parent, name.toUpperCase());
        }

        public UpperSnakeCasePath(String name) {
            this(null, name);
        }

        @Override
        public @NotNull HandlerPath resolve(@NotNull String value) {
            if (value.isEmpty()) {
                return this;
            }

            return new UpperSnakeCasePath(this, value);
        }

    }

    private static final class LowerSnakeCasePath extends SnakeCasePath {

        public LowerSnakeCasePath(SnakeCasePath parent, String name) {
            super(parent, name.toLowerCase());
        }

        public LowerSnakeCasePath(String name) {
            this(null, name);
        }

        @Override
        public @NotNull HandlerPath resolve(@NotNull String value) {
            if (value.isEmpty()) {
                return this;
            }

            return new LowerSnakeCasePath(this, value);
        }

    }


}
