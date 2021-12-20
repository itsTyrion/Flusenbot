package de.itsTyrion.flusenbot.handler

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.ChatPermissions
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.BanChatMember
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.RestrictChatMember
import de.itsTyrion.flusenbot.util.NewMember

/**
 * @author itsTyrion
 * @since 26.04.2019
 */
class UpdateHandler(private val bot: TelegramBot) : UpdatesListener {
    private val messageHandler = MessageHandler(bot)

    override fun process(updates: List<Update>): Int {
        for (update in updates) try {
            if (update.message() != null) {
                messageHandler.handleMessage(update.message())
            } else if (update.callbackQuery() != null) {
                val query = update.callbackQuery()
                val userID = query.from().id()
                val entry = map[userID] ?: continue
                val chatID = query.message().chat().id()

                if (entry.id == query.message().messageId()) {
                    if (entry.expectedInput == query.data()) {
                        map.remove(userID)
                        bot.execute(EditMessageText(chatID, entry.id,
                                "Willkommen, " + query.from().firstName() + "^^"))
                        // unrestrict and give back chat-default permissions
                        bot.execute(RestrictChatMember(chatID, userID, UNRESTRICT))
                    } else if (entry.isFirstTry) {
                        entry.isFirstTry = false
                    } else {
                        bot.execute(BanChatMember(chatID, userID).revokeMessages(true))
                        map.remove(userID)
                        bot.execute(DeleteMessage(chatID, entry.id))
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL
    }

    companion object {
        @JvmField
        val map: MutableMap<Long, NewMember> = HashMap()
        private val UNRESTRICT = ChatPermissions()
                .canSendMessages(true).canSendMediaMessages(true).canSendOtherMessages(true).canAddWebPagePreviews(true)
                .canInviteUsers(true).canSendPolls(true).canChangeInfo(true).canPinMessages(true)
    }
}