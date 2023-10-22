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

package plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.Argument;
import w.commander.platform.paper.PaperCommander;
import w.commander.platform.spigot.SpigotErrorResultFactory;
import w.commander.result.FailedResult;
import w.commander.result.Results;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author whilein
 */
public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        var commander = PaperCommander.builder(this)
                .addCooldownManager(new TestCooldownManager())
                .errorResultFactory(new ErrorResultFactory() {
                    @Override
                    public <E extends Enum<E>> FailedResult onInvalidEnum(
                            @NotNull Argument argument,
                            @NotNull String value,
                            @NotNull Map<@NotNull String, @NotNull E> enumValues
                    ) {
                        return Results.error("wtf " + value + "?! try " + enumValues.keySet());
                    }

                    @Override
                    public @NotNull FailedResult onCooldown(@NotNull Duration remaining) {
                        return Results.error("cooldown " + remaining);
                    }

                    @Override
                    public @NotNull FailedResult onFailBetweenValidation(@NotNull Argument argument, double min, double max) {
                        return Results.error("fail between " + min + " and " + max);
                    }

                    @Override
                    public @NotNull FailedResult onFailGreaterThanValidation(@NotNull Argument argument, double value) {
                        return Results.error("fail greater than " + value);
                    }

                    @Override
                    public @NotNull FailedResult onFailLowerThanValidation(@NotNull Argument argument, double value) {
                        return Results.error("fail lower than " + value);
                    }

                    @Override
                    public @NotNull FailedResult onFailRegexValidation(@NotNull Argument argument, @NotNull Pattern pattern) {
                        return Results.error("fail regex " + pattern);
                    }
                })
                .spigotErrorResultFactory(new SpigotErrorResultFactory() {
                    @Override
                    public @NotNull FailedResult onOfflinePlayer(@NotNull String value) {
                        return Results.error("bro " + value + " is offline :(");
                    }

                    @Override
                    public @NotNull FailedResult onUnknownWorld(@NotNull String value) {
                        return Results.error("world " + value + " not found :<");
                    }

                    @Override
                    public @NotNull FailedResult onFailConsoleOnlyCondition() {
                        return Results.error("console only");
                    }

                    @Override
                    public @NotNull FailedResult onFailNonSelfPlayerValidation(@NotNull String customMessage) {
                        return Results.error("non self " + customMessage);
                    }

                    @Override
                    public @NotNull FailedResult onFailPlayerOnlyCondition() {
                        return Results.error("player only");
                    }
                })
                .addCondition(new PermissionCondition())
                .build();

        commander.register(new TestCommand());
    }

}
