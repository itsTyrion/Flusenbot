package de.itsTyrion.flusenbot.command.commands

import com.pengrad.telegrambot.model.Message
import de.itsTyrion.flusenbot.command.Command

class Ping : Command("ping") {

    override fun execute(args: Array<String>, msg: Message): Boolean {
        val now = System.currentTimeMillis()
        val id = reply(msg.chat(), "Pong!").message().messageId()
        editMessage(msg.chat(), id, "Pong! (" + (System.currentTimeMillis() - now) + "ms)")
        return true
    }
}