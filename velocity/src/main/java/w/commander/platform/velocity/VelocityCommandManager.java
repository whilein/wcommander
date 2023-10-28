package w.commander.platform.velocity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.*;
import w.commander.error.DefaultExecutionThrowableInterceptor;
import w.commander.execution.ExecutionThrowableInterceptor;
import w.commander.manual.ManualFactory;
import w.commander.manual.SimpleManualFactory;
import w.commander.manual.description.DescriptionFactory;
import w.commander.manual.description.SimpleDescriptionFactory;
import w.commander.manual.usage.SimpleUsageFactory;
import w.commander.manual.usage.UsageFactory;
import w.commander.parameter.HandlerParameterResolver;
import w.commander.platform.velocity.error.VelocityErrorResultFactory;
import w.commander.platform.velocity.execution.VelocityExecutionContextFactory;
import w.commander.platform.velocity.internal.VelocityCommandProxy;
import w.commander.spec.AnnotationBasedCommandSpecFactory;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.path.HandlerPathStrategies;
import w.commander.spec.path.HandlerPathStrategy;
import w.commander.spec.template.CommandTemplateFactory;
import w.commander.spec.template.SimpleCommandTemplateFactory;

import java.util.Collections;

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

    public static @NotNull VelocityCommandManager.Builder builder(
            com.velocitypowered.api.command.CommandManager commandManager
    ) {
        return new Builder(commandManager);
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

    @Setter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        com.velocitypowered.api.command.CommandManager velocityCommandManager;

        @NonFinal
        VelocityErrorResultFactory errorResultFactory;

        @NonFinal
        HandlerPathStrategy handlerPathStrategy;

        @NonFinal
        UsageFactory usageFactory;

        @NonFinal
        DescriptionFactory descriptionFactory;

        @NonFinal
        Iterable<? extends @NotNull HandlerParameterResolver> parameterResolvers;

        @NonFinal
        CommandNodeFactory commandNodeFactory;

        @NonFinal
        ExecutionThrowableInterceptor executionThrowableInterceptor;

        @NonFinal
        ManualFactory manualFactory;

        public @NotNull CommandManager build() {
            VelocityErrorResultFactory errorResultFactory;

            if ((errorResultFactory = this.errorResultFactory) == null) {
                errorResultFactory = new VelocityErrorResultFactory() {};
            }

            HandlerPathStrategy handlerPathStrategy;

            if ((handlerPathStrategy = this.handlerPathStrategy) == null) {
                handlerPathStrategy = HandlerPathStrategies.lowerSnakeCase();
            }

            UsageFactory usageFactory;

            if ((usageFactory = this.usageFactory) == null) {
                usageFactory = SimpleUsageFactory.create();
            }

            DescriptionFactory descriptionFactory;

            if ((descriptionFactory = this.descriptionFactory) == null) {
                descriptionFactory = SimpleDescriptionFactory.create();
            }

            Iterable<? extends @NotNull HandlerParameterResolver> parameterResolvers;

            if ((parameterResolvers = this.parameterResolvers) == null) {
                parameterResolvers = Collections.emptySet();
            }

            CommandNodeFactory commandNodeFactory;

            if ((commandNodeFactory = this.commandNodeFactory) == null) {
                commandNodeFactory = SimpleCommandNodeFactory.create(errorResultFactory);
            }

            ExecutionThrowableInterceptor executionThrowableInterceptor;

            if ((executionThrowableInterceptor = this.executionThrowableInterceptor) == null) {
                executionThrowableInterceptor = DefaultExecutionThrowableInterceptor.create(errorResultFactory);
            }

            ManualFactory manualFactory;

            if ((manualFactory = this.manualFactory) == null) {
                manualFactory = SimpleManualFactory.create();
            }

            return new VelocityCommandManager(
                    velocityCommandManager,
                    SimpleCommandTemplateFactory.create(),
                    AnnotationBasedCommandSpecFactory.create(
                            handlerPathStrategy,
                            usageFactory,
                            descriptionFactory,
                            parameterResolvers
                    ),
                    SimpleCommandFactory.create(
                            commandNodeFactory,
                            VelocityExecutionContextFactory.create(),
                            executionThrowableInterceptor,
                            manualFactory
                    )
            );
        }
    }

}
