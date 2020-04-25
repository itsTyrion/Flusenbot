package de.itsTyrion.flusenbot.util;

import lombok.val;

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

    public static void log(String msg) {
        log(msg, Level.INFO);
    }

    public static void warning(String msg) {
        log(msg, Level.WARNING);
    }

    private static void log(String msg, Level level) {
        date.setTime(System.currentTimeMillis());
        val time = '[' + df.format(date) + ']';
        if (level == Level.WARNING) {
            System.out.println(time + '[' + YELLOW + "WARN" + RESET + "] " + YELLOW + msg + RESET);
        } else if (level == Level.SEVERE) {
            System.err.println(msg);
        } else
            System.out.println(time + '[' + level.getName() + "] " + msg);
    }

    public static class ErrStream extends PrintStream {
        public ErrStream() {super(System.err, true);}
        public void println(Object x) {
            date.setTime(System.currentTimeMillis());
            super.println('[' + df.format(date) + "][" + RED + "ERROR" + RESET +  "] " + x);
        }
    }
}
