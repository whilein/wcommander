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

package w.commander.parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.argument.Argument;
import w.commander.util.Immutables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HandlerParameters {

    private static final HandlerParameters EMPTY = new HandlerParameters();

    List<HandlerParameter> parameters;
    List<Argument> arguments;
    int requiredArgumentCount;
    int argumentCount;

    public HandlerParameters() {
        this.parameters = Collections.emptyList();
        this.arguments = Collections.emptyList();
        this.requiredArgumentCount = argumentCount = 0;
    }

    public static @NotNull HandlerParameters from(
            @NotNull HandlerParameter @NotNull ... parameters
    ) {
        if (parameters.length == 0) {
            return EMPTY;
        }

        int requiredArgumentCount = 0;
        int argumentCount = 0;

        val arguments = new ArrayList<Argument>();

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

            arguments.add((Argument) parameter);
        }

        return new HandlerParameters(
                Immutables.immutableList(parameters),
                Immutables.immutableList(arguments),
                requiredArgumentCount,
                argumentCount
        );
    }

}

