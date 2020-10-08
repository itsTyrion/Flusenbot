package de.itsTyrion.flusenbot.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import static de.itsTyrion.flusenbot.util.ANSIColor.*;

/**
 * @author itsTyrion
 * Created on 25/04/2020
 **/
public final class Log {
    private static final Date date = new Date();
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    public static void log(String msg) { log(msg, Level.INFO); }

    public static void warning(String msg) { log(msg, Level.WARNING); }

    public static void error(String msg) { log(msg, Level.SEVERE); }

    private static void log(String msg, Level level) {
        date.setTime(System.currentTimeMillis());
        var time = '[' + df.format(date) + ']';
        if (level == Level.WARNING) {
            System.out.println(time + '[' + YELLOW + "WARN" + RESET + "] " + YELLOW + msg + RESET);
        } else if (level == Level.SEVERE) {
            System.err.println(msg);
        } else
            System.out.println(time + '[' + level.getName() + "] " + msg);
    }

    public static void setup() {
        System.setErr(new ErrStream());
    }

    private static class ErrStream extends PrintStream {
        private ErrStream() { super(System.err, true); }

        //region Ugly hack but functional.
        // Calling String.valueOf(arg) is the PrintStream behavior, overring the next method is not possible, thoug
        @Override public void print(Object x) { print(String.valueOf(x)); }

        @Override public void print(int i) { print(String.valueOf(i)); }

        @Override public void print(char c) { print(String.valueOf(c)); }

        @Override public void print(long l) { print(String.valueOf(l)); }

        @Override public void print(float f) { print(String.valueOf(f)); }

        @Override public void print(double d) { print(String.valueOf(d)); }

        @Override public void print(boolean b) { print(String.valueOf(b)); }
        //endregion

        @Override
        public void print(String s) {
            date.setTime(System.currentTimeMillis());
            super.print('[' + df.format(date) + "][" + RED + "ERR " + RESET + "] " + s);
        }
    }
}