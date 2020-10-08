package de.itsTyrion.flusenbot.command.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import de.itsTyrion.flusenbot.cache.AdminCache;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;

@CommandInfo(name = "reloadadmins", isAdminOnly = true)
public class ReloadAdmins extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        var id = reply(chat, "Reloading").message().messageId();
        AdminCache.refreshAdminCache(chat);
        delete(chat.id(), id);
        reply(chat, "Reloaded!");
        return true;
    }
}
