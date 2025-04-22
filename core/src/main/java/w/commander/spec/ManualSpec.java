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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.annotation.concurrent.Immutable;

/**
 * @author whilein
 */
@Value
@Immutable
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualSpec {

    private static final ManualSpec EMPTY = new ManualSpec(false, null);

    boolean hasHandler;
    ManualSubCommandSpec subCommand;

    public static ManualSpec empty() {
        return EMPTY;
    }

    public static ManualSpec of(boolean handler, ManualSubCommandSpec subCommand) {
        if (isEmpty(handler, subCommand)) {
            return EMPTY;
        }

        return new ManualSpec(handler, subCommand);
    }

    public boolean isEmpty() {
        return isEmpty(hasHandler, subCommand);
    }

    private static boolean isEmpty(boolean handler, ManualSubCommandSpec subCommand) {
        return !handler && subCommand == null;
    }

}
