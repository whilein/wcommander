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

package w.commander.minecraft;

import lombok.experimental.UtilityClass;
import w.commander.minecraft.annotation.ConsoleOnly;
import w.commander.minecraft.annotation.PlayerOnly;
import w.commander.minecraft.condition.ConsoleOnlyCondition;
import w.commander.minecraft.condition.PlayerOnlyCondition;

/**
 * @author whilein
 */
@UtilityClass
public class MinecraftConditions {

    public void install(MinecraftCommanderConfig config) {
        config.addCondition(PlayerOnly.class, new PlayerOnlyCondition(config));
        config.addCondition(ConsoleOnly.class, new ConsoleOnlyCondition(config));
    }

}
