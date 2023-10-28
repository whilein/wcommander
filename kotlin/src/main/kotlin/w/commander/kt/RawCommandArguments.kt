package w.commander.kt

import w.commander.RawCommandArguments

/**
 * @author whilein
 */
object RawCommandArguments {
    @JvmStatic
    inline val RawCommandArguments.size: Int
        get() = size()

    @JvmStatic
    operator fun RawCommandArguments.get(index: Int): String = value(index)

    @JvmStatic
    operator fun RawCommandArguments.get(range: IntRange): RawCommandArguments = subArguments(range.first, range.last + 1)

}
