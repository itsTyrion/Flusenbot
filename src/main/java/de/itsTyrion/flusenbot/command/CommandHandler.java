package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;
import de.itsTyrion.flusenbot.handler.MessageHandler;
import de.itsTyrion.flusenbot.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;

public class CommandHandler {
    private final TelegramBot api;

    public CommandHandler(TelegramBot api) {
        this.api = api;
    }

    public void handleCommand(Message msg) {
        val text = msg.text();
        val chatID = msg.chat().id();

        if (text.equals("!cooldown") ||text.equals("/cooldown")) {
            api.execute(new GetChatAdministrators(chatID), new CooldownCallback(msg.from().id(), chatID));
        } else if (text.startsWith("/")) {
            int id = api.execute(new SendMessage(chatID, "Bald...")).message().messageId();
            Utils.runDelayed(() -> api.execute(new DeleteMessage(chatID, id)), 5);
        }
    }


    @RequiredArgsConstructor
    class CooldownCallback implements Callback<GetChatAdministrators, GetChatAdministratorsResponse> {
        private final int id;
        private final long chatID;
        private static final int ID_TYRION = 218446038;

        @Override
        public void onResponse(GetChatAdministrators request, GetChatAdministratorsResponse response) {
            if (id == ID_TYRION || response.administrators().stream().anyMatch(m -> m.user().id().equals(id))) {
                MessageHandler.addCooldown(chatID);
                if (id != ID_TYRION) {
                    api.execute(new SendMessage(chatID, "Cooldown für 25 Sek. aktiviert! Bei Fehlern @itsTyrion nerven"));
                } else
                    api.execute(new SendMessage(chatID, "Cooldown für 25 Sek. aktiviert!"));

                Utils.runDelayed(() -> MessageHandler.removeCooldown(chatID), 26);
            }
        }

        @Override
        public void onFailure(GetChatAdministrators request, IOException ex) {
            api.execute(new SendMessage(chatID, "Ein Fehler ist aufgetreten!"));
            ex.printStackTrace();
        }
    }
}
