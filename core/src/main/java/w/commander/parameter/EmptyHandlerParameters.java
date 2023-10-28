package w.commander.parameter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import w.commander.parameter.argument.Argument;

import java.util.*;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyHandlerParameters extends AbstractList<HandlerParameter> implements HandlerParameters {

    private static final HandlerParameters INSTANCE = new EmptyHandlerParameters();

    private static final HandlerParameter[] EMPTY_ARRAY = new HandlerParameter[0];

    public static @NotNull HandlerParameters getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull HandlerParameter @NotNull [] toArray() {
        return EMPTY_ARRAY;
    }

    @Override
    public HandlerParameter get(int index) {
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    @Override
    public @NotNull Iterator<@NotNull HandlerParameter> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public @NotNull ListIterator<@NotNull HandlerParameter> listIterator(int index) {
        if (index == 0) {
            return Collections.emptyListIterator();
        }

        return super.listIterator(index);
    }

    @Override
    public Spliterator<HandlerParameter> spliterator() {
        return Spliterators.emptySpliterator();
    }

    @Override
    public @Unmodifiable List<? extends @NotNull Argument> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int getRequiredArgumentCount() {
        return 0;
    }

    @Override
    public int getArgumentCount() {
        return 0;
    }

}

