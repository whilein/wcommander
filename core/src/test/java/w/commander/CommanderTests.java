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

import lombok.val;
import org.junit.jupiter.api.Test;
import w.commander.annotation.Async;
import w.commander.annotation.Attr;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Cooldown;
import w.commander.annotation.SetupHandler;
import w.commander.attribute.AttributeStore;
import w.commander.cooldown.CooldownResult;
import w.commander.result.Result;
import w.commander.result.Results;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author whilein
 */
class CommanderTests {

    @Test
    void attributes() {
        @Command("foo")
        class Attributes {
            @SetupHandler
            public void setup(AttributeStore as) {
                as.setAttribute(String.class, "foo");
            }

            @CommandHandler
            public Result run(@Attr String value) {
                return Results.ok(value);
            }
        }

        val info = new CommandInfo(new Attributes());
        val commander = new Commander();
        val command = commander.register(info);
        val actor = new TestCommandActor("");

        val result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
        assertEquals(Collections.singletonList("foo"), actor.getMessages());
    }

    @Test
    void async() {
        @Command("foo")
        class AsyncCommand {
            @Async
            @CommandHandler
            public Result run() {
                return Results.ok(Thread.currentThread().getName());
            }
        }

        val threadName = "async test";

        val executor = Executors.newSingleThreadExecutor(r -> {
            val thread = new Thread(r);
            thread.setName(threadName);
            return thread;
        });

        val info = new CommandInfo(new AsyncCommand());

        val commander = new Commander();
        commander.setAsyncExecutor(executor);

        val command = commander.register(info);
        val actor = new TestCommandActor("");

        val result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
        assertEquals(Collections.singletonList(threadName), actor.getMessages());
    }

    @Test
    void cooldown() {
        @Command("foo")
        class CooldownCommand {
            @Cooldown
            @CommandHandler
            public Result run() {
                return CooldownResult.of(Results.ok(), Duration.ofSeconds(1));
            }
        }

        val info = new CommandInfo(new CooldownCommand());

        val commander = new Commander();

        val command = commander.register(info);
        val fooActor = new TestCommandActor("foo");
        val barActor = new TestCommandActor("bar");

        Result result = assertDoesNotThrow(() -> command.execute(fooActor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());

        result = assertDoesNotThrow(() -> command.execute(fooActor, RawArguments.empty())
                .get());
        assertFalse(result.isSuccess());

        result = assertDoesNotThrow(() -> command.execute(barActor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());

        assertDoesNotThrow(() -> Thread.sleep(1000));

        result = assertDoesNotThrow(() -> command.execute(fooActor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
    }

}
