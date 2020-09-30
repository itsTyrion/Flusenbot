package de.itsTyrion.flusenbot.cache;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatAdministrators;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.itsTyrion.flusenbot.Flusenbot.api;

public final class AdminCache {
    private static final Map<Chat, List<ChatMember>> adminMap = new ConcurrentHashMap<>();

    public static List<ChatMember> getAdmins(Chat chat) {
        return adminMap.computeIfAbsent(chat, chat1 -> getChatAdmins(chat.id()));
    }

    public static boolean isAdmin(User user, Chat chat) {
        return getAdmins(chat).stream().anyMatch(it -> it.user().equals(user));
    }

    public static void refreshAdminCache(Chat chat) {
        adminMap.put(chat, getChatAdmins(chat.id()));
    }

    private static List<ChatMember> getChatAdmins(long chatID) {
        return api.execute(new GetChatAdministrators(chatID)).administrators();
    }

//    @RequiredArgsConstructor
//    class AdminCallback implements Callback<GetChatAdministrators, GetChatAdministratorsResponse> {
//        private final long chatID;
//
//        public void onResponse(GetChatAdministrators request, GetChatAdministratorsResponse response) {}
//
//        public void onFailure(GetChatAdministrators request, IOException ex) {
//            api.execute(new SendMessage(chatID, "Ein Fehler ist aufgetreten!"));
//            System.err.println("An error occured in chat with id " + chatID);
//            ex.printStackTrace();
//        }
//    }
}