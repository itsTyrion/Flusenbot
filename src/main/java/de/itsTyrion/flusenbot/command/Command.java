package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.model.ChatMember;
import lombok.Getter;

import java.util.function.Function;

public abstract class Command {
    private Permission[] needed;


    @Getter
    public enum Permission {
        CHANGE_INFO(ChatMember::canChangeInfo),
        DELETE_MESSAGES(ChatMember::canDeleteMessages),
        BAN_USERS(ChatMember::canRestrictMembers),
        INVITE_LINK(ChatMember::canInviteUsers),
        PIN_MESSAGES(ChatMember::canPinMessages),
        ADD_ADMINS(ChatMember::canPromoteMembers);

        private final Function<ChatMember, Boolean> check;

        Permission(Function<ChatMember, Boolean> check) {
            this.check = check;
        }
    }
}