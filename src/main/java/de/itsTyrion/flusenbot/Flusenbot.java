package de.itsTyrion.flusenbot;

import com.pengrad.telegrambot.TelegramBot;
import de.itsTyrion.flusenbot.handler.UpdateHandler;
import de.itsTyrion.flusenbot.util.InputThread;
import de.itsTyrion.flusenbot.util.Log;
import lombok.NonNull;

import static de.itsTyrion.flusenbot.util.ANSIColor.*;

/**
 * @author itsTyrion
 * @since 25.04.2019
 */
public final class Flusenbot {
    private static final String TOKEN = //<editor-fold desc=":thinking:">
            "734194746:AAFHaGsQ0LK7iWdNqZ219jBaEtmYlTBIKnY"; //</editor-fold>
    //    private static final long ID_BOTTEST = -1001487242269L;
    private static final String VERSION = "1.2.4";
    public static TelegramBot api;

    public static void main(String... args) {
        var start = System.currentTimeMillis();
        System.out.println(getLogo());
        System.setErr(new Log.ErrStream());

        Log.log("Logging in...");
        api = new TelegramBot(TOKEN);
        Log.log("Login successful.");
        Log.log("Registering Listeners...");
        api.setUpdatesListener(new UpdateHandler(api));

        new InputThread(api).start();

        Log.log("Startup complete after " + (System.currentTimeMillis() - start) + "ms");
    }

    private static @NonNull String getLogo() {
        var base = RED + "\n" +
                " _______  _                 _______  _______  _        ______   _______ _________\n" +
                "(  ____ \\( \\      |\\     /|(  ____ \\(  ____ \\( (    /|(  ___ \\ (  ___  )\\__   __/\n" +
                "| (    \\/| (      | )   ( || (    \\/| (    \\/|  \\  ( || (   ) )| (   ) |   ) (   \n" +
                "| (__    | |      | |   | || (_____ | (__    |   \\ | || (__/ / | |   | |   | |   \n" +
                "|  __)   | |      | |   | |(_____  )|  __)   | (\\ \\) ||  __ (  | |   | |   | |   \n" +
                "| (      | |      | |   | |      ) || (      | | \\   || (  \\ \\ | |   | |   | |   \n" +
                "| )      | (____/\\| (___) |/\\____) || (____/\\| )  \\  || )___) )| (___) |   | |   \n" +
                "|/       (_______/(_______)\\_______)(_______/|/    \\_)|/ \\___/ (_______)   )_(   \n" +
                "\n";

        var toReplace = ")      |";
        var replacement = CYAN + VERSION + substringBefore(toReplace.substring(VERSION.length())) + RED + '|';
        return base.replace(toReplace, ')' + replacement) + RESET + '\n';
    }

    private static @NonNull String substringBefore(@NonNull String str) {
        return str.substring(0, str.indexOf('|') - 1);
    }
}