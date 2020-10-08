package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import de.itsTyrion.flusenbot.Flusenbot;
import de.itsTyrion.flusenbot.util.Utils;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.Function;

@Getter
public abstract class Command {
    protected static final TelegramBot api = Flusenbot.api;

    private final String name;
    private final Permission[] permissions;
    private final boolean adminOnly;

    protected Command() {
        var info = Objects.requireNonNull(getClass().getAnnotation(CommandInfo.class));
        name = info.name();
        permissions = info.permissions();
        adminOnly = info.isAdminOnly();
    }

    protected abstract boolean execute(String[] args, Chat chat, User user);

    protected static SendResponse reply(@NonNull Chat chat, String what) {
        return reply(chat, what, 0);
    }

    protected static SendResponse reply(@NonNull Chat chat, String what, int deleteAfter) {
        var response = api.execute(new SendMessage(chat.id(), what));
        if (deleteAfter > 0)
            Utils.runDelayed(() -> delete(chat.id(), response.message().messageId()), deleteAfter);
        return response;
    }

    protected static void delete(Long chatID, int messageID) { api.execute(new DeleteMessage(chatID, messageID)); }

    protected static void editMessage(@NonNull Chat chat, int messageID, String newText) {
        api.execute(new EditMessageText(chat.id(), messageID, newText));
    }

    @SuppressWarnings({"unused", "RedundantSuppression"}) // it's NOT redundant ffs ._.
    public enum Permission {
        CHANGE_INFO(ChatMember::canChangeInfo),
        DELETE_MESSAGES(ChatMember::canDeleteMessages),
        BAN_USERS(ChatMember::canRestrictMembers),
        INVITE_LINK(ChatMember::canInviteUsers),
        PIN_MESSAGES(ChatMember::canPinMessages),
        ADD_ADMINS(ChatMember::canPromoteMembers);

        private final Function<ChatMember, Boolean> check;

        public boolean check(ChatMember member) {
            return check.apply(member);
        }

        Permission(Function<ChatMember, Boolean> check) {
            this.check = check;
        }
    }
}