package de.itsTyrion.flusenbot;

import com.pengrad.telegrambot.TelegramBot;
import de.itsTyrion.flusenbot.handler.UpdateHandler;
import de.itsTyrion.flusenbot.util.InputThread;
import de.itsTyrion.flusenbot.util.Log;
import lombok.val;

import static de.itsTyrion.flusenbot.util.ANSIColor.*;

/**
 * @author itsTyrion
 * @since 25.04.2019
 */
public final class Flusenbot {
    private static final String TOKEN = //<editor-fold desc=":thinking:">
            "734194746:AAFHaGsQ0LK7iWdNqZ219jBaEtmYlTBIKnY"; //</editor-fold>
// private static final long ID_BOTTEST = -1001487242269L;

    public static void main(String... args) {
        val start = System.currentTimeMillis();
        val v = YELLOW + "1.2" + RED;
        System.out.println(RED + "\n" +
                " _______  _                 _______  _______  _        ______   _______ _________\n" +
                "(  ____ \\( \\      |\\     /|(  ____ \\(  ____ \\( (    /|(  ___ \\ (  ___  )\\__   __/\n" +
                "| (    \\/| (      | )   ( || (    \\/| (    \\/|  \\  ( || (   ) )| (   ) |   ) (   \n" +
                "| (__    | |      | |   | || (_____ | (__    |   \\ | || (__/ / | |   | |   | |   \n" +
                "|  __)   | |      | |   | |(_____  )|  __)   | (\\ \\) ||  __ (  | |   | |   | |   \n" +
                "| (      | |      | |   | |      ) || (      | | \\   || (  \\ \\ | |   | |   | |   \n" +
                "| )      | (____/\\| (___) |/\\____) || (____/\\| )  \\  || )___) )| (___) |   | |   \n" +
                "|/ "+v+" (_______/(_______)\\_______)(_______/|/    )_)|/ \\___/ (_______)   )_(   \n" +
                "\n" + RESET);
        System.setErr(new Log.ErrStream());
        Log.log("Logging in...");
        val api = new TelegramBot(TOKEN);
        Log.log("Login successfull");
        Log.log("Registering Listeners...");
        api.setUpdatesListener(new UpdateHandler(api));
        new InputThread(api).start();
        Log.log("Startup complete after " + (System.currentTimeMillis() - start) + "ms");
    }
}