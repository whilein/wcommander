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
import w.commander.parameter.argument.Argument;
import w.commander.spec.CommandSpec;

import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleUsageFormatter implements UsageFormatter {

    String prefix;

    public SimpleUsageFormatter() {
        this("");
    }

    private void appendCommand(CommandSpec spec, StringBuilder sb) {
        val parent = spec.getParent();

        if (parent != null) {
            appendCommand(parent, sb);
            sb.append(' ');
        }

        sb.append(spec.getName());
    }

    @Override
    public @NotNull FormattingText create(
            @NotNull CommandSpec commandSpec,
            @NotNull List<? extends @NotNull Argument> arguments
    ) {
        val builder = new StringBuilder(prefix);

        appendCommand(commandSpec, builder);

        for (val argument : arguments) {
            builder.append(' ');

            val required = argument.isRequired();

            builder.append(required ? '<' : '(');
            builder.append(argument.getName());
            builder.append(required ? '>' : ')');
        }

        return FormattingText.ofString(builder.toString());
    }

}
