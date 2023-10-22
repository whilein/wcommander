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

package w.commander.executor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.manual.Manual;
import w.commander.manual.ManualEntry;
import w.commander.manual.ManualFormatter;
import w.commander.result.Result;
import w.commander.util.Callback;
import w.commander.util.CallbackArrayCollector;

import java.util.stream.Collectors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class ManualCommandExecutor implements CommandExecutor {

    Manual manual;
    ManualFormatter manualFormatter;

    ErrorResultFactory errorResultFactory;

    @Override
    public void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        val manual = this.manual;

        val name = manual.name();
        val entries = manual.entries();
        val entryCount = entries.size();

        val arrayCollector = new CallbackArrayCollector<ManualEntryResult>(
                callback.map(results -> {
                    val newEntries = results.stream()
                            .filter(r -> r.result.isSuccess())
                            .map(r -> r.entry)
                            .collect(Collectors.toList());

                    if (newEntries.isEmpty()) {
                        return errorResultFactory.onManualUnavailable(manual);
                    }

                    return manualFormatter.format(context, new Manual(name, newEntries));
                }), entryCount);

        int i = 0;
        for (val entry : entries) {
            entry.getConditions().testVisibility(context, arrayCollector.element(i++)
                    .map(result -> new ManualEntryResult(entry, result)));
        }
    }

    @Override
    public void suggest(
            @NotNull ExecutionContext context,
            @NotNull Callback<@Nullable Result> callback
    ) {
        val manual = this.manual;

        val entries = manual.entries();
        val entryCount = entries.size();

        val arrayCollector = new CallbackArrayCollector<Result>(
                callback.map(results -> {
                    for (val result : results) {
                        if (!result.isSuccess()) {
                            return result;
                        }
                    }

                    return null;
                }), entryCount);

        int i = 0;
        for (val entry : manual.entries()) {
            entry.getConditions().testVisibility(context, arrayCollector.element(i++));
        }
    }

    @Override
    public boolean isYielding() {
        return true;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class ManualEntryResult {
        ManualEntry entry;
        Result result;
    }

}
