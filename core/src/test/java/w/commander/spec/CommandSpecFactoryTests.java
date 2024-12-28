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
import org.jetbrains.annotations.NotNull;
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
import w.commander.annotation.SubCommand;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.TabComplete;
import w.commander.annotation.WithAlias;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.annotation.WithManualSubCommand;
import w.commander.attribute.MapAttributeStore;
import w.commander.condition.Condition;
import w.commander.condition.ConditionFactory;
import w.commander.decorator.Decorator;
import w.commander.decorator.DecoratorFactory;
import w.commander.executor.MethodExecutor;
import w.commander.parameter.argument.parser.type.EnumArgumentParser;
import w.commander.parameter.argument.parser.type.NoopArgumentParser;
import w.commander.parameter.argument.parser.type.NumberArgumentParser;
import w.commander.parameter.argument.type.JoinArgument;
import w.commander.parameter.argument.type.OrdinaryArgument;
import w.commander.tabcomplete.ExplicitTabCompleter;
import w.commander.tabcomplete.Suggestions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author whilein
 */
class CommandSpecFactoryTests {

    @Test
    void name() {
        @Command("foo")
        class Name {
        }

        val info = new CommandInfo(new Name());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertSame(info, command.getInfo());
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

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Noop {
    }

    static class NoopDecorator implements Decorator {
        @Override
        public @NotNull MethodExecutor wrap(@NotNull HandlerSpec handler, @NotNull MethodExecutor delegate) {
            return delegate;
        }
    }

    static class NoopDecoratorFactory implements DecoratorFactory<Noop> {

        @Override
        public @NotNull Class<? extends Noop> getAnnotation() {
            return Noop.class;
        }

        @Override
        public @NotNull Decorator create(@NotNull Noop annotation) {
            return new NoopDecorator();
        }
    }

    @Test
    void decorators() {
        @Command("foo")
        class Decorators {
            @Noop
            @CommandHandler
            public void decorator() {
            }
        }


        val info = new CommandInfo(new Decorators());

        val config = CommanderConfig.createDefaults();
        config.addDecorator(new NoopDecoratorFactory());

        val csf = new CommandSpecFactory(config);

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertSame(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertTrue(command.getSubCommands().isEmpty());
        assertTrue(command.getSetupHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());

        val handlers = command.getHandlers();
        assertEquals(1, handlers.size());

        val handler = handlers.get(0);
        assertEquals("foo", handler.getPath().toString());
        assertSame(command, handler.getCommand());
        assertTrue(handler.getConditions().isEmpty());
        assertTrue(handler.getParameters().isEmpty());

        val decorators = handler.getDecorators();
        assertEquals(1, decorators.size());

        val decorator = decorators.get(0);
        assertInstanceOf(NoopDecorator.class, decorator);
    }

    static class NoopCondition implements Condition {
    }

    static class NoopConditionFactory implements ConditionFactory<Noop> {

        @Override
        public @NotNull Class<? extends Noop> getAnnotation() {
            return Noop.class;
        }

        @Override
        public @NotNull Condition create(@NotNull Noop annotation) {
            return new NoopCondition();
        }
    }

    @Test
    void conditions() {
        @Command("foo")
        class Conditions {
            @Noop
            @CommandHandler
            public void condition() {
            }
        }


        val info = new CommandInfo(new Conditions());

        val config = CommanderConfig.createDefaults();
        config.addCondition(new NoopConditionFactory());

        val csf = new CommandSpecFactory(config);

        val command = csf.create(CommandGraph.builder(info)
                .build());
        assertSame(info, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertTrue(command.getSubCommands().isEmpty());
        assertTrue(command.getSetupHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());

        val handlers = command.getHandlers();
        assertEquals(1, handlers.size());

        val handler = handlers.get(0);
        assertEquals("foo", handler.getPath().toString());
        assertSame(command, handler.getCommand());
        assertTrue(handler.getDecorators().isEmpty());
        assertTrue(handler.getParameters().isEmpty());

        val conditions = handler.getConditions();
        assertEquals(1, conditions.size());

        val condition = conditions.get(0);
        assertInstanceOf(NoopCondition.class, condition);
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
        assertSame(info, command.getInfo());
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
            public void tabcomplete(@TabComplete({"foo", "bar", "baz"}) @Arg String value) {
            }
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
    void illegalNames() {
        @Command("f o")
        class IllegalName {
        }

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());
        try {
            csf.create(CommandGraph.builder(new CommandInfo(new IllegalName()))
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid command name: \"f o\"", e.getMessage());
        }

        @WithAlias("b r")
        @Command("foo")
        class IllegalAlias {
        }

        try {
            csf.create(CommandGraph.builder(new CommandInfo(new IllegalAlias()))
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid command name: \"b r\"", e.getMessage());
        }

        @Command("foo")
        class IllegalSubCommandHandlerName {
            @SubCommandHandler("b r")
            public void run() {
            }
        }

        try {
            csf.create(CommandGraph.builder(new CommandInfo(new IllegalSubCommandHandlerName()))
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid command name: \"b r\"", e.getMessage());
        }
    }

    @Test
    void illegalParameters() {
        @Command("foo")
        class IllegalJoin {
            @CommandHandler
            public void join(@Join Long value) {
            }
        }

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());
        try {
            csf.create(CommandGraph.builder(new CommandInfo(new IllegalJoin()))
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("@Join annotation is not allowed on java.lang.Long", e.getMessage());
        }

        @Command("foo")
        class UnknownParameter {
            @CommandHandler
            public void unknown(Object value) {
            }
        }

        try {
            csf.create(CommandGraph.builder(new CommandInfo(new UnknownParameter()))
                    .build());
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("There are no available parameter parsers for java.lang.Object value", e.getMessage());
        }

        @Command("foo")
        class UnknownArgument {
            @CommandHandler
            public void unknown(@Arg Object value) {
            }
        }

        try {
            csf.create(CommandGraph.builder(new CommandInfo(new UnknownArgument()))
                    .build());
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("There are no available argument parsers for class java.lang.Object", e.getMessage());
        }
    }

    @Test
    void subCommands() {
        @Command("foo")
        class ParentCommand {
            @SetupHandler
            public void setup() {
            }
        }

        @SubCommand("bar")
        class InnerCommand {
            @SetupHandler
            public void setup() {
            }
        }

        val parentInfo = new CommandInfo(new ParentCommand());
        val innerInfo = new CommandInfo(new InnerCommand());

        val csf = new CommandSpecFactory(CommanderConfig.createDefaults());

        val command = csf.create(CommandGraph.builder(parentInfo)
                .subCommand(innerInfo)
                .build());
        assertSame(parentInfo, command.getInfo());
        assertEquals("foo", command.getFullName());
        assertEquals("foo", command.getName());
        assertEquals("foo", command.getPath().toString());
        assertTrue(command.getAliases().isEmpty());
        assertTrue(command.getHandlers().isEmpty());
        assertTrue(command.getManual().isEmpty());
        assertNull(command.getParent());

        {
            val subCommands = command.getSubCommands();
            assertEquals(1, subCommands.size());
            val subCommand = subCommands.get(0);
            assertSame(command, subCommand.getParent());
            assertEquals("bar", subCommand.getName());
            assertEquals("foo bar", subCommand.getFullName());
            assertEquals("foo_bar", subCommand.getPath().toString());
            assertTrue(subCommand.getAliases().isEmpty());
            assertTrue(subCommand.getManual().isEmpty());
            assertTrue(subCommand.getHandlers().isEmpty());

            val setupHandlers = subCommand.getSetupHandlers();
            assertEquals(1, setupHandlers.size());
            val setupHandler = setupHandlers.get(0);
            assertSame(subCommand, setupHandler.getCommand());
            assertTrue(setupHandler.getParameters().isEmpty());

            // command mustn't propagate setup handlers to subcommand
            assertNotEquals(command.getSetupHandlers(), subCommand.getSetupHandlers());
        }

        {
            val setupHandlers = command.getSetupHandlers();
            assertEquals(1, setupHandlers.size());
            val setupHandler = setupHandlers.get(0);
            assertSame(command, setupHandler.getCommand());
            assertTrue(setupHandler.getParameters().isEmpty());
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
            assertSame(subCommand, subCommandHandler.getCommand());
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
            assertSame(command, setupHandler.getCommand());
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
