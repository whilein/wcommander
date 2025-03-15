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

package w.commander.decorator.type;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.annotation.Cooldown;
import w.commander.decorator.AnnotationDecoratorFactory;
import w.commander.decorator.Decorator;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CooldownDecoratorFactory extends AnnotationDecoratorFactory<Cooldown> {

    CommanderConfig config;

    public CooldownDecoratorFactory(CommanderConfig config) {
        super(Cooldown.class);

        this.config = config;
    }

    @Override
    protected @NotNull Decorator create(@NotNull Cooldown annotation) {
        String customId = annotation.id();
        if (customId.isEmpty())
            customId = null;

        val cooldownManagerName = annotation.cooldownManager();
        val cooldownManager = config.getCooldownManager(cooldownManagerName);
        if (cooldownManager == null)
            throw new IllegalArgumentException("Unknown cooldown manager: " + cooldownManagerName);

        return new CooldownDecorator(customId, cooldownManager, config);
    }

}
