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

package w.commander.spec;

import lombok.val;
import org.junit.jupiter.api.Test;
import w.commander.CommandGraph;
import w.commander.CommandInfo;
import w.commander.CommanderConfig;
import w.commander.RawArguments;
import w.commander.TestCommandActor;
import w.commander.TestExecutionContext;
import w.commander.annotation.Arg;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Hidden;
import w.commander.annotation.Join;
import w.commander.annotation.NonRequired;
import w.commander.annotation.SetupHandler;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.TabComplete;
import w.commander.annotation.WithAlias;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.annotation.WithManualSubCommand;
import w.commander.attribute.MapAttributeStore;
import w.commander.parameter.argument.parser.type.EnumArgumentParser;
import w.commander.parameter.argument.parser.type.NoopArgumentParser;
import w.commander.parameter.argument.parser.type.NumberArgumentParser;
import w.commander.parameter.argument.type.JoinArgument;
import w.commander.parameter.argument.type.OrdinaryArgument;
import w.commander.tabcomplete.ExplicitTabCompleter;
import w.commander.tabcomplete.Suggestions;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author whilein
 */
class CommandSpecFactoryTests {

    // TODO decorators, conditions

    @Test
    void name() {
        @Command("foo")
        class Name {
        }

        val info = new CommandInfo(new Name());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertEquals(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertTrue(command.getHandlers().isEmpty());
        assertTrue(command.getSubCommands().isEmpty());
        assertTrue(command.getSetupHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());
    }

    @Test
    void nameAndAliases() {
        @Command("foo")
        @WithAlias("bar")
        @WithAlias("baz")
        class NameAndAliases {
        }

        val info = new CommandInfo(new NameAndAliases());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertEquals(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertEquals(Arrays.asList("bar", "baz"), command.getAliases());
        assertTrue(command.getHandlers().isEmpty());
        assertTrue(command.getSubCommands().isEmpty());
        assertTrue(command.getSetupHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());
    }

    @Test
    void parameters() {
        @Command("foo")
        class Parameters {
            @SubCommandHandler("noop")
            public void noopParser(@Arg String value) {
            }

            @SubCommandHandler("number")
            public void numberParser(@Arg Long value) {
            }

            @SubCommandHandler("enum")
            public void enumParser(@Arg TimeUnit unit) {
            }

            @SubCommandHandler("join")
            public void join(@Join String joinedString) {
            }

            @SubCommandHandler("non_required")
            public void nonRequired(@NonRequired @Arg String value) {
            }

            @SubCommandHandler("tabcomplete")
            public void tabcomplete(@TabComplete({"foo", "bar", "baz"}) @Arg String value) {}
        }

        val context = new TestExecutionContext(
                new TestCommandActor(""),
                RawArguments.empty(),
                new MapAttributeStore()
        );

        val info = new CommandInfo(new Parameters());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertSame(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertTrue(command.getHandlers().isEmpty());
        assertTrue(command.getSetupHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());

        val subCommands = command.getSubCommands();
        assertEquals(6, subCommands.size());

        for (val subCommand : subCommands) {
            assertSame(command, subCommand.getParent());
            assertTrue(subCommand.getAliases().isEmpty());
            assertTrue(subCommand.getManual().isEmpty());
            assertTrue(subCommand.getSetupHandlers().isEmpty());

            val handlers = subCommand.getHandlers();
            assertEquals(1, handlers.size());
            val handler = handlers.get(0);
            assertTrue(handler.getConditions().isEmpty());
            assertTrue(handler.getDecorators().isEmpty());
            val parameters = handler.getParameters();
            assertEquals(1, parameters.getArgumentCount());
            val parameter = parameters.getParameters()
                    .get(0);
            switch (subCommand.getName()) {
                case "noop": {
                    assertEquals(1, parameters.getRequiredArgumentCount());
                    assertEquals("foo noop", subCommand.getFullName());
                    assertEquals("foo_noop", subCommand.getPath().toString());
                    assertInstanceOf(OrdinaryArgument.class, parameter);
                    val argument = (OrdinaryArgument) parameter;
                    assertInstanceOf(NoopArgumentParser.class, argument.getParser());
                    assertTrue(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    assertNull(argument.getTabCompleter());
                    break;
                }
                case "number": {
                    assertEquals(1, parameters.getRequiredArgumentCount());
                    assertEquals("foo number", subCommand.getFullName());
                    assertEquals("foo_number", subCommand.getPath().toString());
                    assertInstanceOf(OrdinaryArgument.class, parameter);
                    val argument = (OrdinaryArgument) parameter;
                    assertInstanceOf(NumberArgumentParser.class, argument.getParser());
                    assertTrue(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    assertNull(argument.getTabCompleter());
                    break;
                }
                case "enum": {
                    assertEquals(1, parameters.getRequiredArgumentCount());
                    assertEquals("foo enum", subCommand.getFullName());
                    assertEquals("foo_enum", subCommand.getPath().toString());
                    assertInstanceOf(OrdinaryArgument.class, parameter);
                    val argument = (OrdinaryArgument) parameter;
                    assertInstanceOf(EnumArgumentParser.class, argument.getParser());
                    assertTrue(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    val tabComplete = argument.getTabCompleter();
                    assertInstanceOf(ExplicitTabCompleter.class, tabComplete);

                    val suggestions = new Suggestions();
                    try {
                        tabComplete.getSuggestions(
                                context,
                                "",
                                suggestions
                        );
                    } finally {
                        suggestions.release();
                    }
                    assertEquals(Arrays.asList("days", "hours", "microseconds", "milliseconds", "minutes", "nanoseconds", "seconds"),
                            suggestions.block());
                    break;
                }
                case "join": {
                    assertEquals(1, parameters.getRequiredArgumentCount());
                    assertEquals("foo join", subCommand.getFullName());
                    assertEquals("foo_join", subCommand.getPath().toString());
                    assertInstanceOf(JoinArgument.class, parameter);
                    val argument = (JoinArgument) parameter;
                    assertTrue(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    assertNull(argument.getTabCompleter());
                    break;
                }
                case "non_required": {
                    assertEquals(0, parameters.getRequiredArgumentCount());
                    assertEquals("foo non_required", subCommand.getFullName());
                    assertEquals("foo_non_required", subCommand.getPath().toString());
                    val argument = (OrdinaryArgument) parameter;
                    assertInstanceOf(NoopArgumentParser.class, argument.getParser());
                    assertFalse(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    assertNull(argument.getTabCompleter());
                    break;
                }
                case "tabcomplete": {
                    assertEquals(1, parameters.getRequiredArgumentCount());
                    assertEquals("foo tabcomplete", subCommand.getFullName());
                    assertEquals("foo_tabcomplete", subCommand.getPath().toString());
                    assertInstanceOf(OrdinaryArgument.class, parameter);
                    val argument = (OrdinaryArgument) parameter;
                    assertInstanceOf(NoopArgumentParser.class, argument.getParser());
                    assertTrue(argument.isRequired());
                    assertTrue(argument.getValidators().isEmpty());
                    val tabComplete = argument.getTabCompleter();
                    assertInstanceOf(ExplicitTabCompleter.class, tabComplete);

                    val suggestions = new Suggestions();
                    try {
                        tabComplete.getSuggestions(
                                context,
                                "",
                                suggestions
                        );
                    } finally {
                        suggestions.release();
                    }
                    assertEquals(Arrays.asList("foo", "bar", "baz"), suggestions.block());
                    break;
                }
                default:
                    fail();
            }
        }
    }

    @Test
    void illegalParameters() {
        @Command("foo")
        class IllegalJoin {
            @CommandHandler
            public void join(@Join Long value) {}
        }

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());
        try {
            csf.create(CommandGraph.builder(new CommandInfo(new IllegalJoin()))
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("@Join annotation is not allowed on java.lang.Long", e.getMessage());
        }
    }

    @Test
    void handlers() {
        @Command("foo")
        @WithManual
        @WithManualSubCommand
        class Handlers {
            @SetupHandler
            public void setup() {
            }

            @CommandHandler
            public void unnamed() {
            }

            @CommandHandler("foo")
            public void foo() {
            }

            @WithDescription("custom bar")
            @CommandHandler("bar")
            public void bar() {
            }

            @Hidden
            @CommandHandler("baz")
            public void baz() {
            }

            @SubCommandHandler("foo")
            public void fooSubCommand() {

            }
        }

        val context = new TestExecutionContext(
                new TestCommandActor(""),
                RawArguments.empty(),
                new MapAttributeStore()
        );

        val info = new CommandInfo(new Handlers());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertSame(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertNull(command.getParent());

        {
            val handlers = command.getHandlers();
            assertEquals(4, handlers.size());
            for (val handler : handlers) {
                assertSame(command, handler.getCommand());
                assertTrue(handler.getConditions().isEmpty());
                assertTrue(handler.getDecorators().isEmpty());
                assertTrue(handler.getParameters().isEmpty());

                val manualEntry = handler.getManualEntry();
                assertTrue(manualEntry.getConditions().isEmpty());

                assertEquals("foo", manualEntry.getUsage().format(context));

                switch (handler.getPath().toString()) {
                    case "foo":
                    case "foo_foo":
                        assertFalse(manualEntry.isHidden());
                        assertEquals("", manualEntry.getDescription().format(context));
                        break;
                    case "foo_bar":
                        assertFalse(manualEntry.isHidden());
                        assertEquals("custom bar", manualEntry.getDescription().format(context));
                        break;
                    case "foo_baz":
                        assertTrue(manualEntry.isHidden());
                        assertNull(manualEntry.getDescription());
                        break;
                    default:
                        fail();
                }
            }
        }

        {
            val subCommands = command.getSubCommands();
            assertEquals(1, subCommands.size());
            val subCommand = subCommands.get(0);
            assertSame(command, subCommand.getParent());
            assertEquals("foo", subCommand.getName());
            assertEquals("foo foo", subCommand.getFullName());
            assertEquals("foo_foo", subCommand.getPath().toString());
            assertTrue(subCommand.getAliases().isEmpty());
            assertTrue(subCommand.getManual().isEmpty());
            val subCommandHandlers = subCommand.getHandlers();
            assertEquals(1, subCommandHandlers.size());
            val subCommandHandler = subCommandHandlers.get(0);
            assertEquals(subCommand, subCommandHandler.getCommand());
            assertTrue(subCommandHandler.getConditions().isEmpty());
            assertTrue(subCommandHandler.getDecorators().isEmpty());
            assertTrue(subCommandHandler.getParameters().isEmpty());
            val manualEntry = subCommandHandler.getManualEntry();
            assertTrue(manualEntry.getConditions().isEmpty());
            assertEquals("foo foo", manualEntry.getUsage().format(context));
            assertFalse(manualEntry.isHidden());
            assertEquals("", manualEntry.getDescription().format(context));

            // command must propagate setup handlers to subcommand handlers
            assertEquals(command.getSetupHandlers(), subCommand.getSetupHandlers());
        }

        {
            val setupHandlers = command.getSetupHandlers();
            assertEquals(1, setupHandlers.size());
            val setupHandler = setupHandlers.get(0);
            assertEquals(command, setupHandler.getCommand());
            assertTrue(setupHandler.getParameters().isEmpty());
        }

        {
            val manual = command.getManual();
            assertFalse(manual.isEmpty());
            assertEquals("help", manual.getSubCommand().getName());
            assertTrue(manual.getSubCommand().getAliases().isEmpty());
            assertTrue(manual.isHasHandler());
        }
    }
}
