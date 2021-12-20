package de.itsTyrion.flusenbot.util

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import java.util.*

class InputThread(private val bot: TelegramBot) : Thread() {

    override fun run() {
        val sc = Scanner(System.`in`)

        while (sc.hasNextLine()) {
            try {
                val input = sc.nextLine().trim { it <= ' ' }
                val split = input.split(" ").toTypedArray()
                when (split[0]) {
                    "stop" -> Runtime.getRuntime().exit(0)

                    "send" -> {
                        val joined = split.copyOfRange(2, split.size).joinToString(" ")
                        bot.execute(SendMessage(split[1].toLong(), joined))
                    }
                    "del" -> bot.execute(DeleteMessage(split[1].toLong(), split[2].toInt()))

                    "welcome" -> bot.execute(SendMessage(split[1].toLong(), "Willkommen, " + split[2] + "^^"))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        sc.close()
    }
}