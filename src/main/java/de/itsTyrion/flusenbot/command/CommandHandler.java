package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import de.itsTyrion.flusenbot.cache.AdminCache;
import de.itsTyrion.flusenbot.handler.MessageHandler;
import de.itsTyrion.flusenbot.util.Utils;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class CommandHandler {
    private final @NotNull TelegramBot api;

    private static final int ID_TYRION = 218446038;

    public CommandHandler(@NotNull TelegramBot api) {
        this.api = api;
    }


    public void handleCommand(@NotNull Message msg, @NotNull Chat chat, @NotNull User from) {
        val text = msg.text();
        if (text.contains("@") && !text.contains("Flusenbot")) return;
        val split = text.split(" ");
        val cmd = split[0].substring(1).replaceAll("@[Ff]lusenbot", "");
//        val args = Arrays.copyOfRange(split, 1, split.length);
        val chatID = chat.id();

        switch (cmd) {
            case "cooldown":
                if (!AdminCache.isAdmin(from, chat) && from.id() != ID_TYRION)
                    return;
                MessageHandler.addCooldown(chatID);

                if (from.id() != ID_TYRION) {
                    api.execute(new SendMessage(chatID, "Cooldown für 25 Sek. aktiviert! Bei Fehlern @itsTyrion nerven"));
                } else
                    api.execute(new SendMessage(chatID, "Cooldown für 25 Sek. aktiviert!"));

                Utils.runDelayed(() -> MessageHandler.removeCooldown(chatID), 26);
                break;
            case "reloadadmins":
                if (!AdminCache.isAdmin(from, chat))
                    return;
                AdminCache.refreshAdminCache(chat);
                api.execute(new SendMessage(chatID, "Reloaded! (" + AdminCache.getAdmins(chat).size() + " Admins)"));
                break;
            case "ping": {
                val now = System.currentTimeMillis();
                int id = api.execute(new SendMessage(chatID, "Pong!")).message().messageId();
                api.execute(new EditMessageText(chatID, id, "Pong! (" + (System.currentTimeMillis() - now) + "ms)"));
                break;
            }
            default: {
                int id = api.execute(new SendMessage(chatID, "Bald...")).message().messageId();
                Utils.runDelayed(() -> api.execute(new DeleteMessage(chatID, id)), 5);
                break;
            }
        }
    }
}
