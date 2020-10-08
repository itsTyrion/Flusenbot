package de.itsTyrion.flusenbot.cache;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.itsTyrion.flusenbot.Flusenbot.api;

public final class AdminCache {
    private static final Map<Chat, List<ChatMember>> adminMap = new ConcurrentHashMap<>();

    private static @NonNull List<ChatMember> getAdmins(Chat chat) {
        return adminMap.computeIfAbsent(chat, chat1 -> getChatAdmins(chat.id()));
    }

    public static boolean isAdmin(User user, Chat chat) {
        return getAdmins(chat).stream().anyMatch(it -> it.user().equals(user));
    }

    public static void refreshAdminCache(Chat chat) {
        adminMap.put(chat, getChatAdmins(chat.id()));
    }

    private static @NonNull List<ChatMember> getChatAdmins(long chatID) {
        return api.execute(new GetChatAdministrators(chatID)).administrators();
    }
}