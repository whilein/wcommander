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

package w.commander.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.AbstractParameterParser;
import w.commander.parameter.HandlerParameter;
import w.commander.platform.velocity.annotation.PlayerTarget;
import w.commander.platform.velocity.argument.PlayerArgument;
import w.commander.platform.velocity.parameter.CommandSourceHandlerParameter;

import java.lang.reflect.Parameter;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VelocityParameterParser extends AbstractParameterParser {

    ProxyServer server;
    VelocityErrorResultFactory errorResultFactory;

    @Override
    public boolean isSupported(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(PlayerTarget.class)
               || CommandSource.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public @NotNull HandlerParameter parse(@NotNull Parameter parameter) {
        val type = parameter.getType();

        val playerTarget = parameter.getDeclaredAnnotation(PlayerTarget.class);
        if (playerTarget != null) {
            if (!type.isAssignableFrom(Player.class)) {
                throw new IllegalArgumentException("@PlayerTarget annotation is not allowed on " + type.getName());
            }

            return new PlayerArgument(
                    playerTarget.value(),
                    isRequired(parameter),
                    server,
                    errorResultFactory
            );
        }

        if (CommandSource.class.isAssignableFrom(type)) {
            return CommandSourceHandlerParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }
}
