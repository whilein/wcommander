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

package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import w.commander.annotation.Arg;
import w.commander.annotation.Async;
import w.commander.annotation.Attr;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Cooldown;
import w.commander.annotation.Join;
import w.commander.annotation.NonRequired;
import w.commander.annotation.SetupHandler;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.WithManual;
import w.commander.attribute.AttributeStore;
import w.commander.cooldown.CooldownManager;
import w.commander.cooldown.CooldownResult;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.TypedParameterParser;
import w.commander.result.Result;
import w.commander.result.Results;

import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static w.commander.RawArguments.fromArray;

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
    void asyncParameter() {
        @Command("foo")
        class AsyncParameterCommand {
            @CommandHandler
            public Result run(String parameter) {
                return Results.ok(parameter + " " + Thread.currentThread().getName());
            }
        }
        val info = new CommandInfo(new AsyncParameterCommand());

        class AsyncParameter extends TypedParameterParser<String> {

            public AsyncParameter() {
                super(String.class);
            }

            @Override
            public @NotNull HandlerParameter parse(@NotNull Parameter parameter) {
                return (context, cursor) ->
                        CompletableFuture.supplyAsync(() -> "Hello world!");
            }
        }

        val commander = new Commander();
        commander.setSyncExecutions(true);
        commander.getTypedParameterParsers().add(new AsyncParameter());

        val command = commander.register(info);
        val actor = new TestCommandActor("");

        val result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
        assertEquals(Collections.singletonList("Hello world! WCommander Main"), actor.getMessages());
    }

    @Test
    void asyncCooldown() {
        @Command("foo")
        class AsyncCommand {
            @Async
            @Cooldown
            @CommandHandler
            public Result run() {
                return Results.ok(Thread.currentThread().getName());
            }
        }

        val info = new CommandInfo(new AsyncCommand());

        val commander = new Commander();

        CooldownManager cooldownManager = commander.getCooldownManager("default");
        cooldownManager = spy(cooldownManager);
        commander.addCooldownManager(cooldownManager);

        val command = commander.register(info);
        val actor = new TestCommandActor("");

        val result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
        assertEquals(Collections.singletonList("WCommander Async #1"), actor.getMessages());

        verify(cooldownManager)
                .getCooldown(Mockito.any(), Mockito.any());

        verify(cooldownManager, Mockito.never())
                .getCooldownAsync(Mockito.any(), Mockito.any(), Mockito.any());
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

        val info = new CommandInfo(new AsyncCommand());

        val commander = new Commander();

        val command = commander.register(info);
        val actor = new TestCommandActor("");

        val result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());
        assertEquals(Collections.singletonList("WCommander Async #1"), actor.getMessages());
    }

    @Test
    void arguments() {
        @Command("foo")
        class Arguments {
            @SubCommandHandler("bytep")
            public Result number(@Arg byte v) {
                return Results.ok("byte primitive: " + v);
            }

            @SubCommandHandler("bytew")
            public Result number(@Arg Byte v) {
                return Results.ok("byte wrapper: " + v);
            }

            @SubCommandHandler("shortp")
            public Result number(@Arg short v) {
                return Results.ok("short primitive: " + v);
            }

            @SubCommandHandler("shortw")
            public Result number(@Arg Short v) {
                return Results.ok("short wrapper: " + v);
            }

            @SubCommandHandler("intp")
            public Result number(@Arg int v) {
                return Results.ok("int primitive: " + v);
            }

            @SubCommandHandler("intw")
            public Result number(@Arg Integer v) {
                return Results.ok("int wrapper: " + v);
            }

            @SubCommandHandler("floatp")
            public Result number(@Arg float v) {
                return Results.ok("float primitive: " + v);
            }

            @SubCommandHandler("floatw")
            public Result number(@Arg Float v) {
                return Results.ok("float wrapper: " + v);
            }

            @SubCommandHandler("doublep")
            public Result number(@Arg double v) {
                return Results.ok("double primitive: " + v);
            }

            @SubCommandHandler("doublew")
            public Result number(@Arg Double v) {
                return Results.ok("double wrapper: " + v);
            }

            @SubCommandHandler("longp")
            public Result number(@Arg long v) {
                return Results.ok("long primitive: " + v);
            }

            @SubCommandHandler("longw")
            public Result number(@Arg Long v) {
                return Results.ok("long wrapper: " + v);
            }

            @SubCommandHandler("noop")
            public Result noop(@Arg String v) {
                return Results.ok("noop: " + v);
            }

            @SubCommandHandler("enum")
            public Result enumeration(@Arg TimeUnit v) {
                return Results.ok("enum: " + v);
            }

            @SubCommandHandler("join")
            public Result join(@Join String s) {
                return Results.ok("join: " + s);
            }

        }

        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        @RequiredArgsConstructor
        final class Test {
            RawArguments input;
            String expect;
        }

        val info = new CommandInfo(new Arguments());
        val commander = new Commander();

        val command = commander.register(info);

        val tests = Arrays.asList(
                new Test(fromArray("bytep", "123"), "byte primitive: 123"),
                new Test(fromArray("bytew", "123"), "byte wrapper: 123"),
                new Test(fromArray("shortp", "1234"), "short primitive: 1234"),
                new Test(fromArray("shortw", "1234"), "short wrapper: 1234"),
                new Test(fromArray("intp", "12345"), "int primitive: 12345"),
                new Test(fromArray("intw", "12345"), "int wrapper: 12345"),
                new Test(fromArray("longp", "1234567"), "long primitive: 1234567"),
                new Test(fromArray("longw", "1234567"), "long wrapper: 1234567"),
                new Test(fromArray("floatp", "123.123"), "float primitive: 123.123"),
                new Test(fromArray("floatw", "123.123"), "float wrapper: 123.123"),
                new Test(fromArray("doublep", "123123.123123"), "double primitive: 123123.123123"),
                new Test(fromArray("doublew", "123123.123123"), "double wrapper: 123123.123123"),
                new Test(fromArray("noop", "foobarbaz"), "noop: foobarbaz"),
                new Test(fromArray("join", "foo", "bar", "baz"), "join: foo bar baz"),
                new Test(fromArray("join", "foo", "bar"), "join: foo bar"),
                new Test(fromArray("join", "foo"), "join: foo"),
                new Test(fromArray("enum", "seconds"), "enum: SECONDS"),
                new Test(fromArray("bytep", "foo"), null),
                new Test(fromArray("bytew", "foo"), null),
                new Test(fromArray("shortp", "foo"), null),
                new Test(fromArray("shortw", "foo"), null),
                new Test(fromArray("intp", "foo"), null),
                new Test(fromArray("intw", "foo"), null),
                new Test(fromArray("longp", "foo"), null),
                new Test(fromArray("longw", "foo"), null),
                new Test(fromArray("floatp", "foo.bar"), null),
                new Test(fromArray("floatw", "foo.bar"), null),
                new Test(fromArray("doublep", "foo.bar"), null),
                new Test(fromArray("doublew", "foo.bar"), null),
                new Test(fromArray("enum", "foo"), null)
        );

        for (val test : tests) {
            val actor = new TestCommandActor("foo");

            val result = assertDoesNotThrow(() -> command.execute(actor, test.input)
                    .get());
            if (test.expect == null) {
                assertFalse(result.isSuccess());
            } else {
                assertTrue(result.isSuccess());
                val messages = actor.getMessages();
                assertEquals(Collections.singletonList(test.expect), messages);
            }
        }
    }

    @Test
    void manualAndNonRequiredJoin() {
        @Command("foo")
        @WithManual
        class Attributes {
            @CommandHandler
            public Result run(@NonRequired @Join String value) {
                return Results.ok(value == null ? "" : value);
            }
        }

        val info = new CommandInfo(new Attributes());
        val commander = new Commander();
        val command = commander.register(info);
        val actor = new TestCommandActor("");

        Result result;
        result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.empty())
                .get());
        assertTrue(result.isSuccess());

        result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.fromArray("foo"))
                .get());
        assertTrue(result.isSuccess());

        result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.fromArray("foo", "bar"))
                .get());
        assertTrue(result.isSuccess());

        result = assertDoesNotThrow(() -> command.execute(actor, RawArguments.fromArray("foo", "bar", "baz"))
                .get());
        assertTrue(result.isSuccess());

        assertEquals(Arrays.asList("", "foo", "foo bar", "foo bar baz"), actor.getMessages());
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
