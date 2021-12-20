package de.itsTyrion.flusenbot.util

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author itsTyrion
 * Created on 24/04/2020
 */
object Utils {
    private val exec = ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2)

    // Pattern to seperate numeric characters from non-numeric ones.
    // taken from: http://stackoverflow.com/a/8270824
    private val splitter = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")

    /**
     * Parses time imput from users in the format of "1d"
     *
     * @return -1 if the input was -1 or perma(nent),
     * -2 for wrong input (negative, not a number...),
     * otherwise the time parsed to milliseconds
     */
    fun parseTimeInput(s: String): Long {
        if (s == "-1" || s == "perma" || s == "permanent") return -1L
        val split = splitter.split(s) // the input split in numeric (if it contains numbers) and characters
        val i: Int = try {
            s.toInt()
        } catch (ex: NumberFormatException) {
            return -2
        }
        if (i < 0)
            return -2L

            return when (split[1]) {
            "s" -> i
            "m" -> i * 60
            "h" -> i * 60 * 60
            "d" -> i * 60 * 60 * 24
            "w" -> i * 60 * 60 * 24 * 7
            "mo" -> i * 60 * 60 * 24 * 30
            else -> -2
        }.toLong()
    }

    /**
     * @return The current UNIX time stamp in second; The Telegram API doesn't take the time stamp in MS.
     */
    @JvmStatic
    fun getUnixTime() = Math.toIntExact(System.currentTimeMillis() / 1000)

    /**
     * Executes the runnable outside the main thread after a delay
     */
    @JvmStatic
    fun runDelayed(runnable: Runnable, delaySeconds: Int) {
        exec.schedule(runnable, delaySeconds.toLong(), TimeUnit.SECONDS)
    }

    private val cyrillic = Pattern.compile(".*\\p{InCyrillic}.*")

    fun containsCyrillic(text: String) = cyrillic.matcher(text).matches()

    fun containsArabic(text: String): Boolean =
            text.chars().anyMatch { c: Int -> Character.UnicodeBlock.of(c) === Character.UnicodeBlock.ARABIC }
}