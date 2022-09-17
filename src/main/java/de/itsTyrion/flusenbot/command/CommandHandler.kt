package de.itsTyrion.flusenbot.command

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.GetChatMember
import com.pengrad.telegrambot.request.SendMessage
import de.itsTyrion.flusenbot.cache.AdminCache.isAdmin
import de.itsTyrion.flusenbot.command.commands.*
import de.itsTyrion.flusenbot.util.Utils.runDelayed

class CommandHandler(private val api: TelegramBot) {
    private val commands = mapOf(
            "ping" to Ping(),
            "reloadadmins" to ReloadAdmins(),
            "cooldown" to Cooldown(),
            "kick" to Kick(),
            "mute" to Soon(),
            "ban" to Soon()
    )

    fun handleCommand(msg: Message, chat: Chat, user: User) {
        val text = msg.text()
        if (text.contains("@") && !text.contains("flusenbot"))
            return

        val split = text.split("\\s+").toTypedArray() // Pattern mathing one or more spaces
        val command = commands[split[0].substring(1).replace("@flusenbot", "").lowercase()] ?: return
        var hasPermissions = true

        if (command.permissions.isNotEmpty()) {
            val member = api.execute(GetChatMember(chat.id(), user.id())).chatMember()

            println("check ${member.user().username()}")
            println(member.status())
            Command.Permission.values().forEach {
                try {
                    println(it.check(member))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            hasPermissions = (command.permissions.all { it.check(member) }
                    || command.name == "cooldown" && chat.id() == -1001238995053L && user.id() == ID_TYRION)
        }
        if ((!command.isAdminOnly || isAdmin(user, chat)) && hasPermissions) {
            command.execute(split.copyOfRange(1, split.size), msg)
        } else {
            val id = api.execute(SendMessage(chat.id(), "⚠️No permission")).message().messageId()
            runDelayed({ api.execute(DeleteMessage(chat.id(), id)) }, 5)
        }
    }

    companion object {
        const val ID_TYRION = 218446038L
    }
}