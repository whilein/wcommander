package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.util.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author whilein
 */
public interface RawArguments {

    static @NotNull RawArguments fromList(@NotNull List<@NotNull String> arguments) {
        return new OfList(arguments);
    }

    static @NotNull RawArguments fromArray(@NotNull String @NotNull ... arguments) {
        return new OfArray(arguments, 0, arguments.length);
    }

    static @NotNull RawArguments fromArray(@NotNull String @NotNull [] arguments, int offset, int length) {
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException("Offset and length mustn't be lower than zero");
        }

        if (offset + length > arguments.length) {
            throw new IllegalArgumentException("Array too small for length " + length + " starting at " + offset);
        }

        return new OfArray(arguments, offset, length);
    }

    static @NotNull RawArguments empty() {
        return OfList.EMPTY;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    @NotNull String value(int index);

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

    @NotNull RawArguments subArguments(int fromIndex, int toIndex);

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class OfArray implements RawArguments {
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
    final class OfList implements RawArguments {
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
