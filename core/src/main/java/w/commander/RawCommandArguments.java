package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.util.ArrayUtils;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author whilein
 */
public interface RawCommandArguments {

    static RawCommandArguments fromList(List<String> arguments) {
        return new OfList(arguments);
    }

    static RawCommandArguments fromArray(String... arguments) {
        return new OfArray(arguments, 0, arguments.length);
    }

    static RawCommandArguments fromArray(String[] arguments, int offset, int length) {
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException("Offset and length mustn't be lower than zero");
        }

        if (offset + length > arguments.length) {
            throw new IllegalArgumentException("Array too small for length " + length + " starting at " + offset);
        }

        return new OfArray(arguments, offset, length);
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    String value(int index);

    default String join(String delimiter, int fromIndex, int toIndex) {
        ArrayUtils.checkRange(fromIndex, toIndex, size());

        val joiner = new StringJoiner(delimiter);

        for (int i = fromIndex; i < toIndex; i++) {
            joiner.add(value(i));
        }

        return joiner.toString();
    }

    default String join(String delimiter, int start) {
        return join(delimiter, start, size());
    }

    default String join(String delimiter) {
        return join(delimiter, 0);
    }

    default RawCommandArguments withOffset(int offset) {
        return subArguments(offset, size());
    }

    default RawCommandArguments withLength(int length) {
        return subArguments(0, length);
    }

    RawCommandArguments subArguments(int fromIndex, int toIndex);

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class OfArray implements RawCommandArguments {
        String[] arguments;
        int offset;
        int length;

        @Override
        public int size() {
            return length;
        }

        @Override
        public String value(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }

            return arguments[offset + index];
        }

        @Override
        public RawCommandArguments subArguments(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == size()) {
                return this;
            }

            ArrayUtils.checkRange(fromIndex, toIndex, length);

            return new OfArray(arguments, this.offset + fromIndex, toIndex - fromIndex);
        }

    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class OfList implements RawCommandArguments {

        List<String> arguments;

        @Override
        public int size() {
            return arguments.size();
        }

        @Override
        public String value(int index) {
            return arguments.get(index);
        }

        @Override
        public RawCommandArguments subArguments(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == size()) {
                return this;
            }

            return new OfList(arguments.subList(fromIndex, toIndex));
        }

    }

}
