package w.commander.platform.velocity.parameter;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.AbstractHandlerParameterResolver;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.HandlerParameterResolver;
import w.commander.platform.velocity.annotation.PlayerTarget;
import w.commander.platform.velocity.error.VelocityErrorResultFactory;
import w.commander.platform.velocity.parameter.type.CommandSourceHandlerParameter;
import w.commander.platform.velocity.parameter.type.PlayerArgument;

import java.lang.reflect.Parameter;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityCommandParameterResolver extends AbstractHandlerParameterResolver {

    public static @NotNull HandlerParameterResolver create(
            @NotNull ProxyServer proxyServer,
            @NotNull VelocityErrorResultFactory commandErrorFactory
    ) {
        return new VelocityCommandParameterResolver(proxyServer, commandErrorFactory);
    }

    ProxyServer proxyServer;
    VelocityErrorResultFactory errorResultFactory;

    @Override
    public boolean isSupported(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(PlayerTarget.class)
               || CommandSource.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public @NotNull HandlerParameter resolve(@NotNull Parameter parameter) {
        val type = parameter.getType();

        val playerTarget = parameter.getDeclaredAnnotation(PlayerTarget.class);

        if (playerTarget != null) {
            if (type != Player.class) {
                throw new IllegalArgumentException("@PlayerTarget annotation is not allowed on " + type.getName());
            }

            return PlayerArgument.create(
                    playerTarget.value(),
                    isRequired(parameter),
                    proxyServer,
                    errorResultFactory
            );
        }

        if (CommandSource.class.isAssignableFrom(type)) {
            return CommandSourceHandlerParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }
}
