package w.commander.kt

import w.commander.RawArguments

/**
 * @author whilein
 */
object RawCommandArguments {
    @JvmStatic
    inline val RawArguments.size: Int
        get() = size()

    @JvmStatic
    operator fun RawArguments.get(index: Int): String = value(index)

    @JvmStatic
    operator fun RawArguments.get(range: IntRange): RawArguments = subArguments(range.first, range.last + 1)

}
