package de.itsTyrion.flusenbot.command.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.itsTyrion.flusenbot.command.Command;
import de.itsTyrion.flusenbot.command.CommandInfo;
import de.itsTyrion.flusenbot.handler.MessageHandler;
import de.itsTyrion.flusenbot.util.Utils;

import static de.itsTyrion.flusenbot.command.CommandHandler.ID_TYRION;

@CommandInfo(name = "cooldown", isAdminOnly = true)
public class Cooldown extends Command {

    @Override
    protected boolean execute(String[] args, Chat chat, User user) {
        MessageHandler.addCooldown(chat.id());

        if (user.id() != ID_TYRION) {
            api.execute(new SendMessage(chat.id(), "Cooldown für 25 Sek. aktiviert! Bei Fehlern @itsTyrion nerven"));
        } else
            api.execute(new SendMessage(chat.id(), "Cooldown für 25 Sek. aktiviert!"));

        Utils.runDelayed(() -> MessageHandler.removeCooldown(chat.id()), 26);
        return true;
    }
}
