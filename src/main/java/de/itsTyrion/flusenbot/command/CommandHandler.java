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
import lombok.NonNull;
import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CommandHandler {
    final @NonNull TelegramBot api;
    private final Map<String, Command> commands;

    public static final int ID_TYRION = 218446038;

    public CommandHandler(@NonNull TelegramBot api) {
        this.api = api;
        val map = new HashMap<String, Command>();

        map.put("ping", new Ping());
        map.put("reloadadmins", new ReloadAdmins());
        map.put("cooldown", new Cooldown());
        map.put("kick", new Soon());
        map.put("mute", new Soon());
        map.put("ban", new Soon());

        commands = Collections.unmodifiableMap(map);
    }

    public void handleCommand(@NonNull Message msg, @NonNull Chat chat, @NonNull User user) {
        val text = msg.text();
        if (text.contains("@") && !text.contains("flusenbot"))
            return;

        val split = text.split(" ");
        val command = commands.get(split[0].substring(1).replace("@flusenbot", "").toLowerCase());

        if (command == null)
            return;

        boolean hasPermissions = true;
        if (command.getPermissions().length != 0) {
            val member = api.execute(new GetChatMember(chat.id(), user.id())).chatMember();
            hasPermissions = Arrays.stream(command.getPermissions()).allMatch(permission -> permission.check(member));
//                    || command.getName().equals("cooldown") && chat.id() == -1001238995053L && user.id() == ID_TYRION;
        }

        if ((!command.isAdminOnly() || AdminCache.isAdmin(user, chat)) && hasPermissions) {
            command.execute(Arrays.copyOfRange(split, 1, split.length), chat, user);
        } else {
            int id = api.execute(new SendMessage(chat.id(), "⚠️No permission")).message().messageId();
            Utils.runDelayed(() -> api.execute(new DeleteMessage(chat.id(), id)), 5);
        }
    }
}