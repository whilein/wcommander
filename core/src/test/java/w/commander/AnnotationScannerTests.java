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
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Hidden;
import w.commander.annotation.SetupHandler;
import w.commander.annotation.SubCommand;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.WithAlias;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.annotation.WithManualSubCommand;
import w.commander.annotation.WithManualSubCommandData;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author whilein
 */
class AnnotationScannerTests {

    AnnotationScanner as = new AnnotationScanner();
    
    @Test
    void isHasManual() {
        @WithManual
        class Present {
        }
        
        assertTrue(as.isHasManual(Present.class));
        
        class Absent {
        }
        
        assertFalse(as.isHasManual(Absent.class));
    }

    @Test
    void getManualSubCommand() {
        @WithManualSubCommand
        class Present {
        }

        assertEquals(new WithManualSubCommandData("help"), as.getManualSubCommand(Present.class));

        class Absent {
        }

        assertNull(as.getManualSubCommand(Absent.class));
    }


    @Test
    void isSetupHandler() {
        class Command {
            @SetupHandler
            public void present() {}

            public void absent() {}
        }

        val present = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("present"));
        assertTrue(as.isSetupHandler(present));

        val absent = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("absent"));
        assertFalse(as.isSetupHandler(absent));
    }

    @Test
    void getCommandHandlerName() {
        class Command {
            @CommandHandler("foo")
            public void present() {}

            public void absent() {}
        }

        val present = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("present"));
        assertEquals("foo", as.getCommandHandlerName(present));

        val absent = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("absent"));
        assertNull(as.getCommandHandlerName(absent));
    }

    @Test
    void getSubCommandHandlerName() {
        class Command {
            @SubCommandHandler("foo")
            public void present() {}

            public void absent() {}
        }

        val present = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("present"));
        assertEquals("foo", as.getSubCommandHandlerName(present));

        val absent = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("absent"));
        assertNull(as.getSubCommandHandlerName(absent));
    }

    @Test
    void getCommandName() {
        @Command("foo")
        class Present {
        }

        assertEquals("foo", as.getCommandName(Present.class));

        class Absent {
        }

        assertNull(as.getCommandName(Absent.class));
    }

    @Test
    void getSubCommandName() {
        @SubCommand("foo")
        class Present {
        }

        assertEquals("foo", as.getSubCommandName(Present.class));

        class Absent {
        }

        assertNull(as.getSubCommandName(Absent.class));
    }
    
    @Test
    void isHidden() {
        class Command {
            @Hidden
            public void present() {}

            public void absent() {}
        }

        val present = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("present"));
        assertTrue(as.isHidden(present));

        val absent = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("absent"));
        assertFalse(as.isHidden(absent));
    }

    @Test
    void getDescription() {
        class Command {
            @WithDescription("foo bar baz")
            public void present() {}

            public void absent() {}
        }

        val present = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("present"));
        assertEquals("foo bar baz", as.getDescription(present));

        val absent = assertDoesNotThrow(() -> Command.class.getDeclaredMethod("absent"));
        assertEquals("", as.getDescription(absent));
    }

    @Test
    void getAliases() {
        @WithAlias("foo")
        class One {
        }

        assertEquals(Collections.singletonList("foo"), as.getAliases(One.class));

        @WithAlias("foo")
        @WithAlias("bar")
        @WithAlias("baz")
        class Many {
        }

        assertEquals(Arrays.asList("foo", "bar", "baz"), as.getAliases(Many.class));

        class Absent {
        }

        assertEquals(Collections.emptyList(), as.getAliases(Absent.class));
    }
}
