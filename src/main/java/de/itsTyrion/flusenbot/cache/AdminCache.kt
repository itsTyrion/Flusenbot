package de.itsTyrion.flusenbot.cache

import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.ChatMember
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.GetChatAdministrators
import de.itsTyrion.flusenbot.Flusenbot
import java.util.concurrent.ConcurrentHashMap

object AdminCache {
    private val adminMap = ConcurrentHashMap<Chat, List<ChatMember>>()

    private fun getAdmins(chat: Chat) = adminMap.computeIfAbsent(chat) { getChatAdmins(chat.id()) }

    @JvmStatic
    fun isAdmin(user: User, chat: Chat) = getAdmins(chat).stream().anyMatch { it.user() == user }

    fun refreshAdminCache(chat: Chat) {
        adminMap[chat] = getChatAdmins(chat.id())
    }

    private fun getChatAdmins(chatID: Long) = Flusenbot.api.execute(GetChatAdministrators(chatID)).administrators()
}