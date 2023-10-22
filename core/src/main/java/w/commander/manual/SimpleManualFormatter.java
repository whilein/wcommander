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

package w.commander.manual;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.Results;
import w.commander.result.SuccessResult;

import java.text.MessageFormat;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleManualFormatter implements ManualFormatter {

    public static final @NotNull String DEFAULT_HEADER_FORMAT = "Command {0}:";
    public static final @NotNull String DEFAULT_ENTRY_FORMAT = "{0} - {1}";

    String headerFormat;
    String entryFormat;

    public SimpleManualFormatter() {
        this(DEFAULT_HEADER_FORMAT, DEFAULT_ENTRY_FORMAT);
    }

    private void append(ManualEntry entry, ExecutionContext context, StringBuilder out) {
        if (!entry.isHidden()) {
            out.append('\n').append(MessageFormat.format(entryFormat,
                    entry.getUsage().format(context),
                    entry.getDescription().format(context)));
        }
    }

    @Override
    public @NotNull SuccessResult format(@NotNull ExecutionContext context, @NotNull Manual manual) {
        val builder = new StringBuilder(MessageFormat.format(headerFormat, manual.name()));

        for (val entry : manual.entries()) {
            append(entry, context, builder);
        }

        return Results.ok(builder.toString());
    }

}
