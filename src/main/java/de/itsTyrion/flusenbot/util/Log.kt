package de.itsTyrion.flusenbot.util

import de.itsTyrion.flusenbot.util.ANSIColor.GREEN
import de.itsTyrion.flusenbot.util.ANSIColor.RESET
import de.itsTyrion.flusenbot.util.ANSIColor.YELLOW
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level

/**
 * @author itsTyrion
 * Created on 25/04/2020
 */
object Log {
    private val date = Date()
    private val df = SimpleDateFormat("dd.MM.yy HH:mm:ss")

    fun log(msg: String) = log(msg, Level.INFO)
    fun warning(msg: String) = log(msg, Level.WARNING)
    fun error(msg: String) = log(msg, Level.SEVERE)

    private fun log(msg: String, level: Level) {
        date.time = System.currentTimeMillis()
        val time = '[' + df.format(date) + ']'
        when (level) {
            Level.INFO -> println(time + '(' + GREEN + "INFO" + RESET + ") " + msg)
            Level.WARNING -> println(time + '(' + YELLOW + "WARN" + RESET + ") " + YELLOW + msg + RESET)
            Level.SEVERE -> System.err.println(msg)
            else -> println(time + '(' + level.name + ") " + msg)
        }
    }

    fun setup() {
        System.setErr(ErrStream())
    }

    private class ErrStream : PrintStream(System.err, true) {
        // region Ugly hack but functional.
        // Calling String.valueOf(arg) is the PrintStream behavior, overring the next method is not possible, thoug
        override fun print(x: Any) = print(x.toString())

        override fun print(i: Int) = print(i.toString())

        override fun print(c: Char) = print(c.toString())

        override fun print(l: Long) = print(l.toString())

        override fun print(f: Float) = print(f.toString())

        override fun print(d: Double) = print(d.toString())

        override fun print(b: Boolean) = print(b.toString())

        //endregion
        override fun print(s: String) {
            date.time = System.currentTimeMillis()
            super.print('[' + df.format(date) + "](" + ANSIColor.RED + "ERR " + RESET + ") " + s)
        }
    }
}