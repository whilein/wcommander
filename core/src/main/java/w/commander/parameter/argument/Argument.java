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

package w.commander.parameter.argument;

import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.tabcomplete.TabCompleter;

import java.util.List;

/**
 * @author whilein
 */
public interface Argument extends HandlerParameter {

    ArgumentInfo getInfo();

    default String getName() {
        return getInfo().getName();
    }

    default boolean isRequired() {
        return getInfo().isRequired();
    }

    default TabCompleter getTabCompleter() {
        return getInfo().getTabCompleter();
    }

    default List<ArgumentValidator> getValidators() {
        return getInfo().getValidators();
    }

    default int getMinLength() {
        return 1;
    }

    default int getMaxLength() {
        return 1;
    }

}
