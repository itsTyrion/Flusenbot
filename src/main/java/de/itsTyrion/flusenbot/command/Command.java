package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import de.itsTyrion.flusenbot.Flusenbot;
import lombok.Getter;
import lombok.val;

import java.util.Objects;
import java.util.function.Function;

@Getter
public abstract class Command {
    protected static final TelegramBot api = Flusenbot.api;

    private final String name;
    private final Permission[] permissions;
    private final boolean adminOnly;

    protected Command() {
        val info = Objects.requireNonNull(getClass().getAnnotation(CommandInfo.class));
        name = info.name();
        permissions = info.permissions();
        adminOnly = info.isAdminOnly();
    }

    protected abstract boolean execute(String[] args, Chat chat, User user);

    protected static SendResponse reply(Chat chat, String what) {
        return api.execute(new SendMessage(chat.id(), what));
    }
    protected static void editMessage(Chat chat, int messageID, String newText) {
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