package de.itsTyrion.flusenbot.command.commands

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import de.itsTyrion.flusenbot.command.Command
import de.itsTyrion.flusenbot.command.CommandHandler
import de.itsTyrion.flusenbot.handler.MessageHandler
import de.itsTyrion.flusenbot.util.Utils.runDelayed

class Cooldown : Command("cooldown", isAdminOnly = true) {

    override fun execute(args: Array<String>, msg: Message): Boolean {
        val chat = msg.chat()
        MessageHandler.addCooldown(chat.id())

        if (msg.from().id() != CommandHandler.ID_TYRION) {
            api.execute(SendMessage(chat.id(), "Cooldown für 25 Sek. aktiviert! Bei Fehlern @itsTyrion nerven"))
        } else
            api.execute(SendMessage(chat.id(), "Cooldown für 25 Sek. aktiviert!"))

        runDelayed({ MessageHandler.removeCooldown(chat.id()) }, 26)
        return true
    }
}