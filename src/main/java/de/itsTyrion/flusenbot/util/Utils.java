package de.itsTyrion.flusenbot.util;

import lombok.NonNull;

import java.lang.Character.UnicodeBlock;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author itsTyrion
 * Created on 24/04/2020
 **/
public final class Utils {
    private static final ScheduledThreadPoolExecutor exec =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2);

    // Pattern to seperate numeric characters from non-numeric ones.
    // taken from: http://stackoverflow.com/a/8270824
    private static final Pattern splitter = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    /**
     * Parses time imput from users in the format of "1d"
     *
     * @return -1 if the input was -1 or perma(nent),
     * -2 for wrong input (negative, not a number..),
     * otherwise the time parsed to milliseconds
     */
    public static long parseTimeInput(@NonNull String s) {
        if (s.equals("-1") || s.equals("perma") || s.equals("permanent"))
            return -1L;

        var split = splitter.split(s); // the input split in numeric (if it contains numbers) and characters

        int i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return -2;
        }
        if (i < 0) // negative time input doesn't make any sense for us
            return -2;

        switch (split[1]) {
            case "s":
                return i;
            case "m":
                return i * 60;
            case "h":
                return i * 60 * 60;
            case "d":
                return i * 60 * 60 * 24;
            case "w":
                return i * 60 * 60 * 24 * 7;
            case "mo":
                return i * 60 * 60 * 24 * 30;
            default:
                return -2;
        }
    }

    /**
     * @return The current UNIX time stamp in second; The Telegram API doesn't take the time stamp in MS.
     */
    public static int getUnixTime() { return Math.toIntExact(System.currentTimeMillis() / 1000); }

    /**
     * Executes the runnable outside the main thread after a delay
     */
    public static void runDelayed(Runnable runnable, int delaySeconds) {
        exec.schedule(runnable, delaySeconds, TimeUnit.SECONDS);
    }


    /**
     * @param s The original String
     * @return The original string from start (index 0) to the first occurence of the given char
     */
    public static @NonNull String substringBefore(@NonNull String s, char before) {
        return s.substring(0, s.indexOf(before) - 1);
    }

    public static boolean containsArabic(String text) {
        return text.chars().anyMatch(c -> UnicodeBlock.of(c) == UnicodeBlock.ARABIC);
    }


    private static final Pattern cyrillic = Pattern.compile(".*\\p{InCyrillic}.*");

    public static boolean containsCyrillic(String text) { return cyrillic.matcher(text).matches(); }
}