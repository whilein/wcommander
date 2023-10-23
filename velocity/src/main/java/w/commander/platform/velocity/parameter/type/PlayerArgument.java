package w.commander.platform.velocity.parameter.type;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.cursor.ArgumentCursor;
import w.commander.platform.velocity.error.VelocityErrorResultFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerArgument implements Argument {

    @Getter
    String name;

    @Getter
    boolean required;

    ProxyServer proxyServer;

    VelocityErrorResultFactory errorResultFactory;

    public static @NotNull HandlerParameter create(
            @NotNull String name,
            boolean required,
            @NotNull ProxyServer proxyServer,
            @NotNull VelocityErrorResultFactory errorResultFactory
    ) {
        return new PlayerArgument(name, required, proxyServer, errorResultFactory);
    }

    @Override
    public Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            val name = context.getRawArguments().value(cursor.next());

            return proxyServer.getPlayer(name)
                    .map(Object.class::cast)
                    .orElseGet(() -> errorResultFactory.onOfflineUser(name));
        }

        return null;
    }
}
