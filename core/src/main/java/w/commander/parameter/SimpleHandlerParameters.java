package w.commander.parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import w.commander.annotation.Arg;
import w.commander.parameter.argument.Argument;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Spliterator.*;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleHandlerParameters extends AbstractList<HandlerParameter> implements HandlerParameters {

    HandlerParameter[] parameters;

    @Getter
    int requiredArgumentCount;

    @Getter
    int argumentCount;

    int arguments;

    public static @NotNull HandlerParameters create(
            @NotNull HandlerParameter @NotNull [] parameters
    ) {
        int requiredArgumentCount = 0;
        int argumentCount = 0;
        int arguments = 0;

        for (val parameter : parameters) {
            if (!(parameter instanceof Argument)) {
                continue;
            }

            val argument = (Argument) parameter;
            val argumentLength = argument.getMinLength();

            argumentCount += argumentLength;

            if (argument.isRequired()) {
                requiredArgumentCount += argumentLength;
            }

            arguments++;
        }

        return new SimpleHandlerParameters(
                parameters,
                requiredArgumentCount,
                argumentCount,
                arguments
        );
    }

    @Override
    public @Unmodifiable @NotNull List<? extends @NotNull Argument> getArguments() {
        val result = new ArrayList<Argument>(arguments);

        for (val parameter : parameters) {
            if (parameter instanceof Argument) {
                result.add((Argument) parameter);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public Spliterator<HandlerParameter> spliterator() {
        return Spliterators.spliterator(parameters, ORDERED | IMMUTABLE | NONNULL);
    }

    @Override
    public int size() {
        return parameters.length;
    }

    @Override
    public @NotNull HandlerParameter @NotNull [] toArray() {
        return parameters.clone();
    }

    @Override
    public HandlerParameter get(int index) {
        return parameters[index];
    }

}

