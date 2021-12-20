package de.itsTyrion.flusenbot.command.commands

import com.pengrad.telegrambot.model.Message
import de.itsTyrion.flusenbot.command.Command

class Soon : Command("sOoN") {

    override fun execute(args: Array<String>, msg: Message): Boolean {
        reply(msg.chat(), "Soon™", 5)
        return true
    }
}