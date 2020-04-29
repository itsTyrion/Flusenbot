package de.itsTyrion.flusenbot.cache;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;
import de.itsTyrion.flusenbot.handler.MessageHandler;
import de.itsTyrion.flusenbot.util.Utils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.itsTyrion.flusenbot.Flusenbot.api;

public final class AdminCache {
    private static final Map<Chat, List<ChatMember>> adminMap = new ConcurrentHashMap<>();

    public static List<ChatMember> getAdmins(Chat chat) {
        adminMap.computeIfAbsent(chat, chat1 -> api.execute(new GetChatAdministrators(chat1.id())).administrators());
        return adminMap.get(chat);
    }

    public static boolean isAdmin(User user, Chat chat) {
        return getAdmins(chat).stream().anyMatch(it -> it.user().equals(user));
    }

    public static void refreshAdminCache(Chat chat) {
        adminMap.put(chat, api.execute(new GetChatAdministrators(chat.id())).administrators());
    }


    @RequiredArgsConstructor
    class AdminCallback implements Callback<GetChatAdministrators, GetChatAdministratorsResponse> {
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
            System.err.println("An error occured in chat with id " + chatID);
            ex.printStackTrace();
        }
    }
}