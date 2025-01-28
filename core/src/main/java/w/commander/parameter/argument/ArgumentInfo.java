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

package w.commander.parameter.argument;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.tabcomplete.TabCompleter;

import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArgumentInfo {

    String name;
    boolean required;
    List<ArgumentValidator> validators;
    TabCompleter tabCompleter;

    public ArgumentInfo(String name) {
        this.name = name;
        this.required = true;
        this.validators = Collections.emptyList();
        this.tabCompleter = null;
    }
}
