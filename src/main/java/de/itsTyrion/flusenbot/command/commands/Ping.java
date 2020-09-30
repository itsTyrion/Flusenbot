package de.itsTyrion.flusenbot.command.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;

@CommandInfo(name = "ping")
public class Ping extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        long now = System.currentTimeMillis();
        int id = reply(chat, "Pong!").message().messageId();
        editMessage(chat, id, "Pong! (" + (System.currentTimeMillis() - now) + "ms)");
        return true;
    }
}
