package de.itsTyrion.flusenbot;

import com.pengrad.telegrambot.TelegramBot;
import de.itsTyrion.flusenbot.handler.UpdateHandler;
import de.itsTyrion.flusenbot.util.InputThread;
import de.itsTyrion.flusenbot.util.Log;
import de.itsTyrion.flusenbot.util.Utils;

import static de.itsTyrion.flusenbot.util.ANSIColor.*;

/**
 * @author itsTyrion
 * @since 25.04.2019
 */
public final class Flusenbot {
    private static final String version = Flusenbot.class.getPackage().getImplementationVersion();
    public static TelegramBot api;

    public static void main(String... args) {
        var start = System.currentTimeMillis();
        System.out.println(getLogo());
        Log.setup();

        Log.log("Logging in...");
        var token = System.getenv("BOT_TOKEN");
        if (token == null) {
            Log.error("Token not set as 'BOT_TOKEN'");
            Log.warning("Configuration issue. Shutting down in 5s.");
            Utils.runDelayed(() -> System.exit(1), 5);
            return;
        }
        api = new TelegramBot(token);
        Log.log("Logged in");
        Log.log("Registering Listeners...");
        api.setUpdatesListener(new UpdateHandler(api));

        new InputThread(api).start();

        Log.log("Startup complete after " + (System.currentTimeMillis() - start) + "ms");
    }

    private static String getLogo() {
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
        var replacement = CYAN + version + Utils.substringBefore(toReplace.substring(version.length()), '|') + RED + '|';
        return base.replace(toReplace, ')' + replacement) + RESET + '\n';
    }
}