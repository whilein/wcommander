package w.commander.platform.velocity.actor;

import com.velocitypowered.api.command.CommandSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * @author _Novit_ (novitpw)
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleVelocityCommandActor implements VelocityCommandActor {

    CommandSource source;

    public static @NotNull VelocityCommandActor create(@NotNull CommandSource source) {
        return new SimpleVelocityCommandActor(source);
    }
}
