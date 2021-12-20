package de.itsTyrion.flusenbot.command.commands

import com.pengrad.telegrambot.model.Message
import de.itsTyrion.flusenbot.cache.AdminCache
import de.itsTyrion.flusenbot.command.Command

class ReloadAdmins : Command("reloadadmins", isAdminOnly = true) {

    override fun execute(args: Array<String>, msg: Message): Boolean {
        val chat = msg.chat()
        val id = reply(chat, "Reloading").message().messageId()
        AdminCache.refreshAdminCache(chat)
        delete(chat.id(), id)
        reply(chat, "Reloaded!")
        return true
    }
}