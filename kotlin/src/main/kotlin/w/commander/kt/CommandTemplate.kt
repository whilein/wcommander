package w.commander.kt

import w.commander.spec.template.CommandTemplate
import w.commander.spec.template.SimpleCommandTemplate

/**
 * @author whilein
 */
object CommandTemplate {

    @JvmStatic
    fun Any.withSubCommand(instance: Any): CommandTemplate {
        return withSubCommand(SimpleCommandTemplate.create(instance))
    }

    @JvmStatic
    fun Any.withSubCommand(subCommand: CommandTemplate): CommandTemplate {
        return SimpleCommandTemplate.create(this, listOf(subCommand))
    }

    @JvmStatic
    fun CommandTemplate.withSubCommand(subCommand: CommandTemplate): CommandTemplate {
        val subCommands = mutableListOf<CommandTemplate>()
        subCommands.addAll(this.subCommands)
        subCommands.add(subCommand)

        return SimpleCommandTemplate.create(this.instance, subCommands)
    }

    @JvmStatic
    fun CommandTemplate.withSubCommand(instance: Any): CommandTemplate {
        return withSubCommand(SimpleCommandTemplate.create(instance))
    }

}