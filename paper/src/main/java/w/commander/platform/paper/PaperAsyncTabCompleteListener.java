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

package w.commander.platform.paper;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import w.commander.RawArguments;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author _Novit_ (novitpw)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaperAsyncTabCompleteListener implements org.bukkit.event.Listener {

    private static final Pattern SPACE = Pattern.compile(" +");

    PaperCommanderConfig commanderConfig;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
        if (!event.isCommand()) return;

        val rawArguments = SPACE.split(event.getBuffer(), -1);
        val rawArgumentsLength = rawArguments.length;
        if (rawArgumentsLength == 1) return;

        val commandName = rawArguments[0].substring(1);
        val spigotCommand = commanderConfig.getCommandRegistrar().getCommand(commandName);
        if (spigotCommand == null) return;

        val actor = commanderConfig.getSpigotCommandActorFactory().create(event.getSender());
        val arguments = RawArguments.fromTrustedArray(rawArguments, 1, rawArgumentsLength - 1);

        try {
            event.setCompletions(spigotCommand.getCommand().tabComplete(actor, arguments)
                    .get()); // надеемся, что Future уже завершен
            event.setHandled(true); // необходимо, чтобы у команды не вызвался tabComplete
        } catch (InterruptedException | ExecutionException e) {
            log.debug("Error while executing async command completions {}", commandName, e);
        }
    }

}
