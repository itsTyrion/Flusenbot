package de.itsTyrion.flusenbot.command.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;

@CommandInfo(name = "sOoN")
public class Soon extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        reply(chat, "Soon™", 5);
        return true;
    }
}
