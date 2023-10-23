package plugin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import w.commander.SimpleCommandFactory;
import w.commander.SimpleCommandNodeFactory;
import w.commander.error.DefaultExecutionThrowableInterceptor;
import w.commander.manual.SimpleManualFactory;
import w.commander.manual.description.SimpleDescriptionFactory;
import w.commander.manual.usage.SimpleUsageFactory;
import w.commander.parameter.DefaultHandlerParameterResolver;
import w.commander.platform.velocity.VelocityCommandManager;
import w.commander.platform.velocity.error.VelocityErrorResultFactory;
import w.commander.platform.velocity.execution.VelocityExecutionContextFactory;
import w.commander.platform.velocity.parameter.VelocityCommandParameterResolver;
import w.commander.spec.AnnotationBasedCommandSpecFactory;
import w.commander.spec.path.HandlerPathStrategies;
import w.commander.spec.path.HandlerPathStrategy;
import w.commander.spec.template.SimpleCommandTemplateFactory;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author whilein
 */
@Plugin(
        id = "test-command-plugin"
)
public class TestPlugin {

    @Inject
    public TestPlugin(ProxyServer proxyServer, CommandManager commandManager) {
        val s = new VelocityErrorResultFactory() {};

        val t = VelocityCommandManager.create(
                commandManager,
                SimpleCommandTemplateFactory.create(),
                AnnotationBasedCommandSpecFactory.create(
                        HandlerPathStrategies.lowerSnakeCase(),
                        SimpleUsageFactory.create(),
                        SimpleDescriptionFactory.create(),
                        Arrays.asList(
                                VelocityCommandParameterResolver.create(
                                        proxyServer,
                                        s
                                ),
                                DefaultHandlerParameterResolver.create(
                                        s
                                )
                        )
                ),
                SimpleCommandFactory.create(
                        SimpleCommandNodeFactory.create(
                                s
                        ),
                        VelocityExecutionContextFactory.create(),
                        DefaultExecutionThrowableInterceptor.create(
                                s
                        ),
                        SimpleManualFactory.create()
                )
        );

        t.register(new TestCommand());
    }
}
