package w.commander.parameter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import w.commander.parameter.argument.Argument;

import java.util.List;
import java.util.RandomAccess;

/**
 * @author whilein
 */
public interface HandlerParameters extends List<@NotNull HandlerParameter>, RandomAccess {

    @Unmodifiable List<? extends @NotNull Argument> getArguments();

    int getRequiredArgumentCount();

    int getArgumentCount();

    @Override
    @NotNull HandlerParameter @NotNull [] toArray();

}
