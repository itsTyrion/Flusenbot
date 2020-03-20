package de.itsTyrion.flusenbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.val;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;

import static de.itsTyrion.flusenbot.util.ANSIColor.*;

/**
 * @author itsTyrion
 * @since 25.04.2019
 */
public final class Flusenbot {
    private static final String TOKEN = //<editor-fold desc=":thinking:">
            "734194746:AAFHaGsQ0LK7iWdNqZ219jBaEtmYlTBIKnY"; //</editor-fold>
    private static final long ID_FLUSENALLEE = -1001119936231L;
    private static final long ID_ATELIER = -1001238995053L;
    // private static final long ID_BOTTEST = -1001487242269L;

    public static void main(String... args) {
        val v = YELLOW + "1.1.1" + RED;
        System.out.println(RED + "\n" +
                " _______  _                 _______  _______  _        ______   _______ _________\n" +
                "(  ____ \\( \\      |\\     /|(  ____ \\(  ____ \\( (    /|(  ___ \\ (  ___  )\\__   __/\n" +
                "| (    \\/| (      | )   ( || (    \\/| (    \\/|  \\  ( || (   ) )| (   ) |   ) (   \n" +
                "| (__    | |      | |   | || (_____ | (__    |   \\ | || (__/ / | |   | |   | |   \n" +
                "|  __)   | |      | |   | |(_____  )|  __)   | (\\ \\) ||  __ (  | |   | |   | |   \n" +
                "| (      | |      | |   | |      ) || (      | | \\   || (  \\ \\ | |   | |   | |   \n" +
                "| )      | (____/\\| (___) |/\\____) || (____/\\| )  \\  || )___) )| (___) |   | |   \n" +
                "|/ "+v+" (_______/(_______)\\_______)(_______/|/    )_)|/ \\___/ (_______)   )_(   \n" +
                "                                                                                 \n" + RESET);
        val bot = new TelegramBot(TOKEN);
        log("Login successfull");
        bot.setUpdatesListener(new UpdateListener(bot));
        log("Registered Listener...");
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String input = sc.nextLine().trim();
                if (input.startsWith("!")) {
                    val split = input.split(" ");
                    val joined = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

                    if (split[0].equals("!fluff")) {
                        bot.execute(new SendMessage(ID_FLUSENALLEE, joined));
                    } else if (split[0].equals("!atelier")) {
                        bot.execute(new SendMessage(ID_ATELIER, joined));
                    } else if (split[0].equals("!delf")) {
                        bot.execute(new DeleteMessage(ID_FLUSENALLEE, Integer.parseInt(split[1])));
                    } else if (split[0].equals("!dela")) {
                        bot.execute(new DeleteMessage(ID_ATELIER, Integer.parseInt(split[1])));
                    } else if (split[0].equals("!wf")) {
                        bot.execute(new SendMessage(ID_FLUSENALLEE, "Willkommen, " + split[1] + "^^"));
                    } else if (split[0].equals("!wa")) {
                        bot.execute(new SendMessage(ID_ATELIER, "Willkommen, " + split[1] + "^^"));
                    }
                }
            }
        }).start();
    }

    private static final Date date = new Date();
    private static final DateFormat df = new SimpleDateFormat("dd.MM. HH:mm:ss");

    static void log(String msg) {
        log(msg, Level.INFO);
    }

    static void log(String msg, Level level) {
        date.setTime(System.currentTimeMillis());
        val time = '[' + df.format(date) + ']';
        if (level == Level.WARNING) {
            System.out.println(time + '[' + YELLOW + "WARN" + RESET + "] " + YELLOW + msg + RESET);
        } else if (level == Level.SEVERE) {
            System.out.println(time + '[' + RED + "ERROR" + RESET +  "] " + RED + msg + RESET);
        } else {
            System.out.println(time + '[' + level.getName() + "] " + msg);
        }
    }
}