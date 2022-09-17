package de.itsTyrion.flusenbot.command.commands

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.BanChatMember
import com.pengrad.telegrambot.request.UnbanChatMember
import de.itsTyrion.flusenbot.command.Command
import de.itsTyrion.flusenbot.util.Utils

class Kick : Command("kick", arrayOf(Permission.BAN_USERS), true) {

    override fun execute(args: Array<String>, msg: Message): Boolean {
        println("kick")
        val target = msg.replyToMessage()?.from() ?: return false
        val chatID = msg.chat().id()

        api.execute(BanChatMember(chatID, target.id()).untilDate(Utils.getUnixTime() + 32).revokeMessages(false))
        api.execute(UnbanChatMember(chatID, target.id()))

        reply(msg.chat(), "YEET")
        return true
    }
}