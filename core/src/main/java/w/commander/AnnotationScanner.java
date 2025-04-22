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

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.Hidden;
import w.commander.annotation.SetupHandler;
import w.commander.annotation.SubCommand;
import w.commander.annotation.SubCommandHandler;
import w.commander.annotation.WithAlias;
import w.commander.annotation.WithAliases;
import w.commander.annotation.WithDescription;
import w.commander.annotation.WithManual;
import w.commander.annotation.WithManualSubCommand;
import w.commander.annotation.WithManualSubCommandData;

import javax.annotation.concurrent.Immutable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@Immutable
public class AnnotationScanner {

    public boolean isHasManual(@NotNull Class<?> type) {
        return type.isAnnotationPresent(WithManual.class);
    }

    public boolean isSetupHandler(@NotNull Method method) {
        return method.isAnnotationPresent(SetupHandler.class);
    }

    public @Nullable String getCommandHandlerName(@NotNull Method method) {
        val commandHandler = method.getDeclaredAnnotation(CommandHandler.class);
        if (commandHandler == null) return null;

        return commandHandler.value();
    }

    public @Nullable String getSubCommandHandlerName(@NotNull Method method) {
        val subCommandHandler = method.getDeclaredAnnotation(SubCommandHandler.class);
        if (subCommandHandler == null) return null;

        return subCommandHandler.value();
    }

    public @Nullable String getCommandName(@NotNull Class<?> type) {
        val command = type.getDeclaredAnnotation(Command.class);
        if (command == null) return null;

        return command.value();
    }

    public @Nullable String getSubCommandName(@NotNull Class<?> type) {
        val subCommand = type.getDeclaredAnnotation(SubCommand.class);
        if (subCommand == null) return null;

        return subCommand.value();
    }

    public @Nullable WithManualSubCommandData getManualSubCommand(@NotNull Class<?> type) {
        return WithManualSubCommandData.of(type.getDeclaredAnnotation(WithManualSubCommand.class));
    }

    public boolean isHidden(@NotNull Method method) {
        return method.isAnnotationPresent(Hidden.class);
    }

    public @NotNull String getDescription(@NotNull Method method) {
        val description = method.getDeclaredAnnotation(WithDescription.class);
        if (description == null) return "";

        return description.value();
    }

    public @Unmodifiable @NotNull List<@NotNull String> getAliases(@NotNull AnnotatedElement element) {
        val alias = element.getDeclaredAnnotation(WithAlias.class);

        if (alias != null) {
            return Collections.singletonList(alias.value());
        }

        val aliases = element.getDeclaredAnnotation(WithAliases.class);

        if (aliases != null) {
            return Collections.unmodifiableList(Arrays.stream(aliases.value())
                    .map(WithAlias::value)
                    .collect(Collectors.toList()));
        }

        return Collections.emptyList();
    }

}
