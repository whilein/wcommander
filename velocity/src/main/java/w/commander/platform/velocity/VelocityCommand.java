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

package w.commander.platform.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.Command;
import w.commander.RawArguments;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author whilein
 */
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class VelocityCommand implements SimpleCommand {
    Command command;
    VelocityCommanderConfig config;

    @Override
    public boolean hasPermission(Invocation invocation) {
        val actor = config.getVelocityCommandActorFactory().create(invocation.source());

        try {
            val result = command.test(actor)
                    .get(); // надеемся, что Future уже завершен

            return result.isSuccess();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        val actor = config.getVelocityCommandActorFactory().create(invocation.source());
        val arguments = RawArguments.fromTrustedArray(invocation.arguments());

        return command.tabComplete(actor, arguments);
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        val actor = config.getVelocityCommandActorFactory().create(invocation.source());
        val arguments = RawArguments.fromTrustedArray(invocation.arguments());

        command.execute(actor, arguments);
    }
}
