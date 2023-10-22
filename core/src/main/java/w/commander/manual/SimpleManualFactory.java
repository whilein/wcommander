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
import w.commander.spec.CommandSpec;
import w.commander.spec.HandlerSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleManualFactory implements ManualFactory {

    private static final Comparator<HandlerSpec> HANDLER_SPEC_COMPARATOR = Comparator.comparing(HandlerSpec::toString);

    private void scanHandlers(CommandSpec spec, List<HandlerSpec> result) {
        result.addAll(spec.getHandlers());

        for (val subCommand : spec.getSubCommands()) {
            scanHandlers(subCommand, result);
        }
    }

    @Override
    public @NotNull Manual create(@NotNull CommandSpec spec) {
        val name = spec.getName();

        val handlers = new ArrayList<HandlerSpec>();
        scanHandlers(spec, handlers);

        val entries = Collections.unmodifiableList(handlers.stream()
                .sorted(HANDLER_SPEC_COMPARATOR)
                .map(HandlerSpec::getManualEntry)
                .collect(Collectors.toList()));

        return new Manual(name, entries);
    }

}
