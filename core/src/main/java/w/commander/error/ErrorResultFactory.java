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

package w.commander.error;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.manual.FormattingText;
import w.commander.manual.Manual;
import w.commander.parameter.argument.Argument;
import w.commander.result.FailedResult;
import w.commander.result.Results;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author whilein
 */
public interface ErrorResultFactory {

    @NotNull
    ErrorResultFactory NOOP = new ErrorResultFactory() {
    };

    default @NotNull FailedResult onManualUnavailable(
            @NotNull ExecutionContext context,
            @NotNull Manual manual
    ) {
        return Results.error("Manual is unavailable for you");
    }

    default @NotNull FailedResult onInternalError(
            @NotNull ExecutionContext context,
            @NotNull Throwable throwable
    ) {
        return Results.error("Internal error occurred");
    }

    default @NotNull FailedResult onNotEnoughArguments(
            @NotNull ExecutionContext context,
            @NotNull FormattingText usage
    ) {
        return Results.error("Not enough arguments");
    }

    default @NotNull FailedResult onInvalidNumber(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            @NotNull String value
    ) {
        return Results.error("Invalid number passed");
    }

    default <E extends Enum<E>> FailedResult onInvalidEnum(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            @NotNull String value,
            @NotNull Map<@NotNull String, @NotNull E> enumValues
    ) {
        return Results.error("Invalid enum passed");
    }

    default @NotNull FailedResult onFailBetweenValidation(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            double inputValue,
            double min,
            double max
    ) {
        return Results.error("Invalid number passed");
    }

    default @NotNull FailedResult onFailGreaterThanValidation(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            double inputValue,
            double minValue
    ) {
        return Results.error("Invalid number passed");
    }

    default @NotNull FailedResult onFailLowerThanValidation(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            double inputValue,
            double maxValue
    ) {
        return Results.error("Invalid number passed");
    }

    default @NotNull FailedResult onFailRegexValidation(
            @NotNull ExecutionContext context,
            @NotNull Argument argument,
            @NotNull Pattern pattern
    ) {
        return Results.error("Invalid value passed");
    }

    default @NotNull FailedResult onCooldown(
            @NotNull ExecutionContext context,
            @NotNull Duration remaining
    ) {
        return Results.error("Too quick!");
    }

}
