package de.itsTyrion.flusenbot.handler

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.Keyboard
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.BanChatMember
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.RestrictChatMember
import com.pengrad.telegrambot.request.SendMessage
import de.itsTyrion.flusenbot.command.CommandHandler
import de.itsTyrion.flusenbot.util.Log
import de.itsTyrion.flusenbot.util.NewMember
import de.itsTyrion.flusenbot.util.Utils
import kotlin.random.Random

class MessageHandler internal constructor(private val bot: TelegramBot) {

    private val board: Keyboard = InlineKeyboardMarkup(arrayOf(
            InlineKeyboardButton("😃").callbackData("1"),
            InlineKeyboardButton("😁").callbackData("2"),
            InlineKeyboardButton("🙃").callbackData("3")
    ), arrayOf(
            InlineKeyboardButton("😉").callbackData("4"),
            InlineKeyboardButton("😗").callbackData("5"),
            InlineKeyboardButton("😌").callbackData("6")
    ), arrayOf(
            InlineKeyboardButton("😜").callbackData("7"),
            InlineKeyboardButton("😛").callbackData("8"),
            InlineKeyboardButton("🤔").callbackData("9")
    ))
    private val commandHandler = CommandHandler(bot)

    fun handleMessage(msg: Message) {
        if (msg.text() != null) {

            if (msg.text().startsWith("/")) {
                commandHandler.handleCommand(msg, msg.chat(), msg.from())
            }
        } else if (msg.newChatMembers() != null && !cooldown.contains(msg.chat().id())) {
            for (user in msg.newChatMembers())
                onMemberJoin(msg, user, msg.from().id() == user.id())
        }
    }

    private fun onMemberJoin(msg: Message, user: User, captcha: Boolean) {
        if (captcha) {
            Log.log("Join: " + user.firstName() + "; " + "sending captcha...")
        } else {
            Log.log("Added: " + user.firstName() + "; " + "skipping captcha.")
            return
        }
        val chatID = msg.chat().id()

        // mute new members until they pass the anti bot
        bot.execute(RestrictChatMember(chatID, user.id()).untilDate(Utils.getUnixTime() + 31))

        val button = Random.nextInt(9) + 1 // get a random button, there's 9 and Telegram counts from 1 :eyeroll:
        val idText = positionIntToDescription(button) // get the user-friendly button description
        val request = SendMessage(chatID, String.format(GREETING, user.firstName(), user.id(), idText))
                .parseMode(ParseMode.Markdown)
                .replyToMessageId(msg.messageId())
                .replyMarkup(board)

        val id = bot.execute(request).message().messageId()
        UpdateHandler.map[user.id()] = NewMember(id, "" + button) // store the expected result

        Utils.runDelayed({ // If there hasn't been a correct input after 30 seconds, bye.
            if (UpdateHandler.map.containsKey(user.id()) && !cooldown.contains(chatID)) {
                // I've never seen a bot rejoin. Also prevents users being false banned for i.e. Telegram crashing
                bot.execute(BanChatMember(chatID, user.id()).untilDate(Utils.getUnixTime() + 32).revokeMessages(true))
                bot.execute(DeleteMessage(chatID, id)) // Delete the welcome message
            }
        }, 31)
    }

    companion object {
        private val cooldown = HashSet<Long>()

        fun addCooldown(chatID: Long) {
            cooldown.add(chatID)
        }

        fun removeCooldown(chatID: Long) {
            cooldown.remove(chatID)
        }

        private const val GREETING = "@[%s](tg://user?id=%d)Bitte wähle das %s Emoji zum verifizieren. \n" +
                "Du hast 30 Sek. Zeit. 2x Fehleingabe/Zeitüberschreitung = Temp-Ban (Anti Bot)"

        private fun positionIntToDescription(pos: Int) = when (pos) {
            1 -> "obere, linke"
            2 -> "obere, mittige"
            3 -> "obere, rechte"
            4 -> "mittlere, linke"
            5 -> "mittlere"
            6 -> "mittlere, rechte"
            7 -> "untere, linke"
            8 -> "untere, mittige"
            9 -> "untere, rechte"
            else -> throw AssertionError()
        }
    }
}