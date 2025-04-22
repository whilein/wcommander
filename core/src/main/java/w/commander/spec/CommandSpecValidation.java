/*
 *    Copyright 2025 Whilein
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

package w.commander.spec;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author whilein
 */
@UtilityClass
public class CommandSpecValidation {

    private final Pattern VALID_COMMAND_NAME = Pattern.compile("^[^ ]+$");

    public void checkCommandNames(@NotNull Iterable<@NotNull String> names) {
        for (val name : names) {
            checkCommandName(name);
        }
    }

    public void checkCommandName(@NotNull String name) {
        if (!VALID_COMMAND_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid command name: \"" + name + "\"");
        }
    }

}
