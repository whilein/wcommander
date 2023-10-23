package w.commander.platform.velocity.internal;

import com.velocitypowered.api.command.SimpleCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.Command;
import w.commander.RawArguments;
import w.commander.platform.velocity.actor.SimpleVelocityCommandActor;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VelocityCommandProxy implements SimpleCommand {

    Command command;

    @Override
    public void execute(@NotNull Invocation invocation) {
        val actor = SimpleVelocityCommandActor.create(invocation.source());
        val arguments = RawArguments.fromArray(invocation.arguments());

        command.execute(actor, arguments);
    }
}
