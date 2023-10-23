package w.commander.platform.velocity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.AbstractCommandManager;
import w.commander.Command;
import w.commander.CommandFactory;
import w.commander.CommandManager;
import w.commander.platform.velocity.internal.VelocityCommandProxy;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.template.CommandTemplateFactory;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class VelocityCommandManager extends AbstractCommandManager {

    com.velocitypowered.api.command.CommandManager velocityCommandManager;

    private VelocityCommandManager(
            com.velocitypowered.api.command.CommandManager velocityCommandManager,
            CommandTemplateFactory commandTemplateFactory,
            CommandSpecFactory commandSpecFactory,
            CommandFactory commandFactory
    ) {
        super(commandTemplateFactory, commandSpecFactory, commandFactory);

        this.velocityCommandManager = velocityCommandManager;
    }

    public static @NotNull CommandManager create(
            @NotNull com.velocitypowered.api.command.CommandManager commandManager,
            @NotNull CommandTemplateFactory commandTemplateFactory,
            @NotNull CommandSpecFactory commandSpecFactory,
            @NotNull CommandFactory commandFactory
    ) {
        return new VelocityCommandManager(
                commandManager,
                commandTemplateFactory,
                commandSpecFactory,
                commandFactory
        );
    }

    @Override
    protected void register(Command command) {
        val commandMeta = velocityCommandManager.metaBuilder(command.getName())
                .aliases(command.getAliases().toArray(String[]::new))
                .build();

        velocityCommandManager.register(commandMeta, new VelocityCommandProxy(command));
    }

    @Override
    protected void unregister(Command command) {
        velocityCommandManager.unregister(command.getName());
    }
}
