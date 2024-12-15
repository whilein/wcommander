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

package w.commander.minecraft;

import org.jetbrains.annotations.NotNull;
import w.commander.result.FailedResult;
import w.commander.result.Results;

/**
 * @author whilein
 */
public interface MinecraftErrorResultFactory {

    default @NotNull FailedResult onFailPlayerOnlyCondition() {
        return Results.error("Console cannot use this command");
    }

    default @NotNull FailedResult onFailConsoleOnlyCondition() {
        return Results.error("Player cannot use this command");
    }

    default @NotNull FailedResult onFailNonSelfPlayerValidation(@NotNull String customMessage) {
        return customMessage.isEmpty()
                ? Results.error("You cannot pass yourself as value")
                : Results.error(customMessage);
    }

    default @NotNull FailedResult onOfflinePlayer(@NotNull String value) {
        return Results.error("Player is offline now");
    }

}
