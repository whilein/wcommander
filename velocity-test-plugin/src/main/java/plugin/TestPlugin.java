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

package plugin;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.Argument;
import w.commander.platform.velocity.VelocityCommander;
import w.commander.platform.velocity.VelocityErrorResultFactory;
import w.commander.result.FailedResult;
import w.commander.result.Results;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author whilein
 */
@Plugin(
        id = "test-command-plugin"
)
public class TestPlugin {

    @Inject
    public TestPlugin(ProxyServer proxyServer) {
        var commander = new VelocityCommander(this, proxyServer);

        commander.setErrorResultFactory(new ErrorResultFactory() {
            @Override
            public <E extends Enum<E>> FailedResult onInvalidEnum(
                    @NotNull Argument argument,
                    @NotNull String value,
                    @NotNull Map<@NotNull String, @NotNull E> enumValues
            ) {
                return Results.error("wtf " + value + "?! try " + enumValues.keySet());
            }
        });

        commander.setVelocityErrorResultFactory(new VelocityErrorResultFactory() {
            @Override
            public @NotNull FailedResult onOfflinePlayer(@NotNull String value) {
                return Results.error("bro " + value + " is offline :(");
            }
        });

        commander.register(new TestCommand());
    }
}
