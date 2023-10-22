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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.util.Preconditions;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Неизменяемое представление массива аргументов без обработки.
 *
 * @author whilein
 */
@ThreadSafe
public interface RawArguments {

    /**
     * Создать представление аргументов из доверенного списка. Список не будет клонирован, поэтому
     * его модификации могут отразиться на представлении аргументов.
     * <p>
     * Создание представления с помощью этого метода <b>не гарантирует</b>
     * его неизменяемость (соответственно потокобезопасность).
     *
     * @param arguments доверенный список аргументов
     * @return представление аргументов
     */
    static @NotNull RawArguments fromTrustedList(@NotNull List<@NotNull String> arguments) {
        return new OfList(arguments);
    }

    /**
     * Создать неизменяемое представление аргументов из списка. Список будет клонирован, его последующие
     * модификации никак не отразятся на представлении аргументов.
     *
     * @param arguments список аргументов
     * @return неизменяемое представление аргументов
     */
    static @NotNull RawArguments fromList(@NotNull List<@NotNull String> arguments) {
        return fromTrustedList(new ArrayList<>(arguments));
    }

    /**
     * Создать представление аргументов из доверенного массива. Массив не будет клонирован, поэтому
     * его модификации могут отразиться на представлении аргументов.
     * <p>
     * Создание представления с помощью этого метода <b>не гарантирует</b>
     * его неизменяемость (соответственно потокобезопасность).
     *
     * @param arguments доверенный массив аргументов
     * @return представление аргументов
     */
    static @NotNull RawArguments fromTrustedArray(@NotNull String @NotNull ... arguments) {
        return new OfArray(arguments, 0, arguments.length);
    }

    /**
     * Создать неизменяемое представление аргументов из массива. Массив будет клонирован, его последующие
     * модификации никак не отразятся на представлении аргументов.
     *
     * @param arguments массив аргументов
     * @return неизменяемое представление аргументов
     */
    static @NotNull RawArguments fromArray(@NotNull String @NotNull ... arguments) {
        return fromTrustedArray(Arrays.copyOf(arguments, arguments.length));
    }

    /**
     * Создать представление аргументов из доверенного массива. Массив не будет клонирован, поэтому
     * его модификации могут отразиться на представлении аргументов.
     * <p>
     * Создание представления с помощью этого метода <b>не гарантирует</b>
     * его неизменяемость (соответственно потокобезопасность).
     *
     * @param arguments доверенный массив аргументов
     * @param offset    отступ аргументов
     * @param length    длина аргументов
     * @return представление аргументов
     */
    static @NotNull RawArguments fromTrustedArray(@NotNull String @NotNull [] arguments, int offset, int length) {
        Preconditions.checkRange(offset, offset + length, arguments.length);

        return new OfArray(arguments, offset, length);
    }

    /**
     * Создать неизменяемое представление аргументов из массива. Массив будет клонирован, его последующие
     * модификации никак не отразятся на представлении аргументов.
     *
     * @param arguments массив аргументов
     * @param offset    отступ аргументов
     * @param length    длина аргументов
     * @return неизменяемое представление аргументов
     */
    static @NotNull RawArguments fromArray(@NotNull String @NotNull [] arguments, int offset, int length) {
        Preconditions.checkRange(offset, offset + length, arguments.length);

        return fromTrustedArray(Arrays.copyOfRange(arguments, offset, offset + length));
    }

    static @NotNull RawArguments empty() {
        return OfList.EMPTY;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    @NotNull
    String value(int index);

    default @NotNull String join(@NotNull String delimiter, int fromIndex, int toIndex) {
        Preconditions.checkRange(fromIndex, toIndex, size());

        val joiner = new StringJoiner(delimiter);

        for (int i = fromIndex; i < toIndex; i++) {
            joiner.add(value(i));
        }

        return joiner.toString();
    }

    default @NotNull String join(@NotNull String delimiter, int fromIndex) {
        return join(delimiter, fromIndex, size());
    }

    default @NotNull String join(@NotNull String delimiter) {
        return join(delimiter, 0);
    }

    default @NotNull RawArguments withOffset(int offset) {
        return subArguments(offset, size());
    }

    default @NotNull RawArguments withLength(int length) {
        return subArguments(0, length);
    }

    @NotNull
    RawArguments subArguments(int fromIndex, int toIndex);

    abstract class RawArgumentsBase implements RawArguments {

        @Override
        public String toString() {
            return "RawArguments{" + join(" ") + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof RawArguments)) return false;

            val that = (RawArguments) o;

            int length;
            if ((length = this.size()) != that.size()) return false;

            for (int i = 0; i < length; i++)
                if (!value(i).equals(that.value(i))) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 1;

            for (int i = 0, j = size(); i < j; i++) {
                hash = hash * 31 + value(i).hashCode();
            }

            return hash;
        }

    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class OfArray extends RawArgumentsBase {
        String[] arguments;
        int offset;
        int length;

        @Override
        public int size() {
            return length;
        }

        @Override
        public @NotNull String value(int index) {
            if (index < 0 || index >= length) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }

            return arguments[offset + index];
        }

        @Override
        public @NotNull RawArguments subArguments(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == size()) {
                return this;
            }

            Preconditions.checkRange(fromIndex, toIndex, length);

            return new OfArray(arguments, this.offset + fromIndex, toIndex - fromIndex);
        }

    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class OfList extends RawArgumentsBase {
        private static final RawArguments EMPTY = new OfList(Collections.emptyList());

        List<String> arguments;

        @Override
        public int size() {
            return arguments.size();
        }

        @Override
        public @NotNull String value(int index) {
            return arguments.get(index);
        }

        @Override
        public @NotNull RawArguments subArguments(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == size()) {
                return this;
            }

            return new OfList(arguments.subList(fromIndex, toIndex));
        }

    }

}
