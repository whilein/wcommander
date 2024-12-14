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

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import w.commander.Commander;
import w.commander.CommanderConfig;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VelocityCommander extends Commander {

    @Delegate(types = VelocityCommanderConfig.class, excludes = CommanderConfig.class)
    VelocityCommanderConfig velocityConfig;

    public VelocityCommander(VelocityCommanderConfig config) {
        super(config);

        this.velocityConfig = config;
    }

    public VelocityCommander(Object plugin, ProxyServer server) {
        this(VelocityCommanderConfig.createDefaults(plugin, server));
    }

}
