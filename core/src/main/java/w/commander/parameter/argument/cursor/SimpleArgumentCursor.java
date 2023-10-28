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
