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

package w.commander;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.manual.Manual;
import w.commander.result.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
public interface Command {

    @NotNull String getName();

    @NotNull List<@NotNull String> getAliases();

    @NotNull
    CompletableFuture<@NotNull List<String>> tabComplete(@NotNull CommandActor actor, @NotNull RawArguments args);

    @NotNull CompletableFuture<@NotNull Result> execute(@NotNull CommandActor actor, @NotNull RawArguments args);

    @NotNull Object getInstance();

    @NotNull Class<?> getInstanceType();

    @Nullable Manual getManual();

}
