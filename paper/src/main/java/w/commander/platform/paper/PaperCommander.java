/*
 *    Copyright 2025 Whilein
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

package w.commander.platform.paper;

import lombok.experimental.Delegate;
import w.commander.CommanderConfig;
import w.commander.platform.spigot.SpigotCommander;
import w.commander.platform.spigot.SpigotCommanderConfig;

/**
 * @author whilein
 */
public class PaperCommander extends SpigotCommander {

    @Delegate(types = PaperCommanderConfig.class, excludes = {SpigotCommanderConfig.class, CommanderConfig.class})
    PaperCommanderConfig paperConfig;

    public PaperCommander(PaperCommanderConfig config) {
        super(config);

        this.paperConfig = config;
    }

    public PaperCommander() {
        this(PaperCommanderConfig.createDefaults());
    }
}
