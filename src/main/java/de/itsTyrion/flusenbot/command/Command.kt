package de.itsTyrion.flusenbot.command

import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.ChatMember
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse
import de.itsTyrion.flusenbot.Flusenbot
import de.itsTyrion.flusenbot.util.Utils.runDelayed

abstract class Command protected constructor(
        val name: String,
        val permissions: Array<Permission> = emptyArray(),
        val isAdminOnly: Boolean = false
) {

    abstract fun execute(args: Array<String>, msg: Message): Boolean

    @Suppress("unused")
    enum class Permission(private val check: (ChatMember) -> Boolean) {
        CHANGE_INFO(ChatMember::canChangeInfo),
        DELETE_MESSAGES(ChatMember::canDeleteMessages),
        BAN_USERS(ChatMember::canRestrictMembers),
        INVITE_LINK(ChatMember::canInviteUsers),
        PIN_MESSAGES(ChatMember::canPinMessages),
        ADD_ADMINS(ChatMember::canPromoteMembers);

        fun check(member: ChatMember) = check.invoke(member)
    }

    companion object {
        @JvmStatic
        protected val api = Flusenbot.api

        @JvmStatic
        protected fun reply(chat: Chat, what: String, deleteAfter: Int = 0): SendResponse {
            val response = api.execute(SendMessage(chat.id(), what))
            if (deleteAfter > 0) runDelayed({ delete(chat.id(), response.message().messageId()) }, deleteAfter)
            return response
        }

        @JvmStatic
        protected fun delete(chatID: Long, messageID: Int) {
            api.execute(DeleteMessage(chatID, messageID))
        }

        @JvmStatic
        protected fun editMessage(chat: Chat, messageID: Int, newText: String?) {
            api.execute(EditMessageText(chat.id(), messageID, newText))
        }
    }
}