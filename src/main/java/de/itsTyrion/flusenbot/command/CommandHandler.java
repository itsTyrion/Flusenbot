package de.itsTyrion.flusenbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import de.itsTyrion.flusenbot.cache.AdminCache;
import de.itsTyrion.flusenbot.command.commands.Cooldown;
import de.itsTyrion.flusenbot.command.commands.Ping;
import de.itsTyrion.flusenbot.command.commands.ReloadAdmins;
import de.itsTyrion.flusenbot.command.commands.Soon;
import de.itsTyrion.flusenbot.util.Utils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Map;

@AllArgsConstructor
public final class CommandHandler {
    private final @NonNull TelegramBot api;
    private final Map<String, Command> commands = Map.of(
            "ping", new Ping(),
            "reloadadmins", new ReloadAdmins(),
            "cooldown", new Cooldown(),
            "kick", new Soon(),
            "mute", new Soon(),
            "ban", new Soon()
    );

    public static final int ID_TYRION = 218446038;

    public void handleCommand(@NonNull Message msg, @NonNull Chat chat, @NonNull User user) {
        var text = msg.text();
        if (text.contains("@") && !text.contains("flusenbot"))
            return;

        var split = text.split("\\s+"); // Pattern mathing one or more spaces
        var command = commands.get(split[0].substring(1).replace("@flusenbot", "").toLowerCase());

        if (command == null)
            return;

        boolean hasPermissions = true;
        if (command.getPermissions().length != 0) {
            var member = api.execute(new GetChatMember(chat.id(), user.id())).chatMember();
            hasPermissions = Arrays.stream(command.getPermissions()).allMatch(permission -> permission.check(member))
                    || command.getName().equals("cooldown") && chat.id() == -1001238995053L && user.id() == ID_TYRION;
        }

        if ((!command.isAdminOnly() || AdminCache.isAdmin(user, chat)) && hasPermissions) {
            command.execute(Arrays.copyOfRange(split, 1, split.length), chat, user);
        } else {
            int id = api.execute(new SendMessage(chat.id(), "⚠️No permission")).message().messageId();
            Utils.runDelayed(() -> api.execute(new DeleteMessage(chat.id(), id)), 5);
        }
    }
}