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

package plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.platform.paper.PaperCommander;
import w.commander.platform.spigot.SpigotCommandInfo;
import w.commander.platform.spigot.SpigotErrorResultFactory;
import w.commander.platform.spigot.SpigotExecutionContext;
import w.commander.result.FailedResult;
import w.commander.result.Results;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whilein
 */
public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        var commander = new PaperCommander();
        commander.addCondition(new PermissionCondition());
        commander.setupAsyncTabCompleteListener(this);

        commander.setErrorResultFactory(new ErrorResultFactory() {
            @Override
            public <E extends Enum<E>> FailedResult onInvalidEnum(
                    @NotNull ExecutionContext context,
                    @NotNull Argument argument,
                    @NotNull String value,
                    @NotNull Map<@NotNull String, @NotNull E> enumValues
            ) {
                return Results.error("wtf " + value + "?! try " + enumValues.keySet());
            }

            @Override
            public @NotNull FailedResult onCooldown(
                    @NotNull ExecutionContext context,
                    @NotNull Duration remaining
            ) {
                return Results.error("cooldown " + remaining);
            }

            @Override
            public @NotNull FailedResult onFailBetweenValidation(
                    @NotNull ExecutionContext context,
                    @NotNull Argument argument,
                    double input,
                    double min,
                    double max
            ) {
                return Results.error("fail between, expected " + min + " and " + max + ", but was " + input);
            }

            @Override
            public @NotNull FailedResult onFailGreaterThanValidation(
                    @NotNull ExecutionContext context,
                    @NotNull Argument argument,
                    double input,
                    double value
            ) {
                return Results.error("fail greater than, expected " + value + ", but was " + input);
            }

            @Override
            public @NotNull FailedResult onFailLowerThanValidation(
                    @NotNull ExecutionContext context,
                    @NotNull Argument argument,
                    double input,
                    double value
            ) {
                return Results.error("fail lower than, expected " + value + ", but was " + input);
            }

            @Override
            public @NotNull FailedResult onFailRegexValidation(
                    @NotNull ExecutionContext context,
                    @NotNull Argument argument,
                    @NotNull String value,
                    @NotNull Pattern pattern,
                    @NotNull Matcher matcher
            ) {
                return Results.error("fail regex " + pattern + ", input " + value);
            }
        });
        commander.setSpigotErrorResultFactory(new SpigotErrorResultFactory() {
            @Override
            public @NotNull FailedResult onOfflinePlayer(
                    @NotNull ExecutionContext context,
                    @NotNull String value
            ) {
                return Results.error("bro " + value + " is offline :(");
            }

            @Override
            public @NotNull FailedResult onUnknownWorld(
                    @NotNull SpigotExecutionContext context,
                    @NotNull String value
            ) {
                return Results.error("world " + value + " not found :<");
            }

            @Override
            public @NotNull FailedResult onFailConsoleOnlyCondition(@NotNull ExecutionContext context) {
                return Results.error("console only");
            }

            @Override
            public @NotNull FailedResult onFailNonSelfPlayerValidation(
                    @NotNull ExecutionContext context,
                    @NotNull String customMessage
            ) {
                return Results.error("non self " + customMessage);
            }

            @Override
            public @NotNull FailedResult onFailPlayerOnlyCondition(@NotNull ExecutionContext context) {
                return Results.error("player only");
            }
        });

        commander.register(this, new TestCommand());
        commander.register(this, new TestPermCommand());
        commander.register(this, new TestPerm2Command());
        commander.register(new SpigotCommandInfo(this, MethodHandles.lookup(), new TestProtectedCommand()));
    }

}
