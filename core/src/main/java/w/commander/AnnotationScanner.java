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
import org.jetbrains.annotations.NotNull;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Hidden;
import w.commander.annotation.SubCommand;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.WithAlias;
import w.commander.annotation.WithAliases;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.annotation.WithManualSubCommand;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * @author whilein
 */
public class AnnotationScanner {

    public boolean isHasManual(@NotNull Class<?> type) {
        return type.isAnnotationPresent(WithManual.class);
    }

    public CommandHandler getCommandHandler(@NotNull Method method) {
        return method.getDeclaredAnnotation(CommandHandler.class);
    }

    public SubCommandHandler getSubCommandHandler(@NotNull Method method) {
        return method.getDeclaredAnnotation(SubCommandHandler.class);
    }

    public Command getCommand(@NotNull Class<?> type) {
        return type.getDeclaredAnnotation(Command.class);
    }

    public SubCommand getSubCommand(@NotNull Class<?> type) {
        return type.getDeclaredAnnotation(SubCommand.class);
    }

    public WithManualSubCommand getManualSubCommand(@NotNull Class<?> type) {
        return type.getDeclaredAnnotation(WithManualSubCommand.class);
    }

    public boolean isHidden(@NotNull Method method) {
        return method.isAnnotationPresent(Hidden.class);
    }

    public WithDescription getDescription(@NotNull Method method) {
        return method.getDeclaredAnnotation(WithDescription.class);
    }

    public WithAlias[] getAliases(@NotNull AnnotatedElement element) {
        val alias = element.getDeclaredAnnotation(WithAlias.class);

        if (alias != null) {
            return new WithAlias[]{alias};
        }

        val aliases = element.getDeclaredAnnotation(WithAliases.class);

        if (aliases != null) {
            return aliases.value();
        }

        return new WithAlias[0];
    }
}
