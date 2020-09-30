package de.itsTyrion.flusenbot.command.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;
import de.itsTyrion.flusenbot.util.Utils;

@CommandInfo(name = "sOoN")
public class Soon extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        int id = reply(chat, "Soon™").message().messageId();
        Utils.runDelayed(() -> api.execute(new DeleteMessage(chat.id(), id)), 5);
        return true;
    }
}
