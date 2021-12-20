package de.itsTyrion.flusenbot

import com.pengrad.telegrambot.TelegramBot
import de.itsTyrion.flusenbot.handler.UpdateHandler
import de.itsTyrion.flusenbot.util.ANSIColor
import de.itsTyrion.flusenbot.util.InputThread
import de.itsTyrion.flusenbot.util.Log
import de.itsTyrion.flusenbot.util.Utils
import kotlin.system.exitProcess

/**
 * @author itsTyrion
 * @since 25.04.2019
 */
object Flusenbot {
    @JvmStatic
    lateinit var api: TelegramBot
    private val version = Flusenbot::class.java.getPackage().implementationVersion ?: "DEBUG"

    @JvmStatic
    fun main(args: Array<String>) {
        val start = System.currentTimeMillis()
        println(getLogo())
        Log.setup()
        Log.log("Logging in...")
        val token = System.getenv("BOT_TOKEN")
        if (token == null) {
            Log.error("Token not set as 'BOT_TOKEN'")
            Log.warning("Configuration issue. Shutting down in 5s.")
            Utils.runDelayed({ exitProcess(1) }, 5)
            return
        }
        api = TelegramBot.Builder(token).updateListenerSleep(200).build()
        Log.log("Logged in")
        Log.log("Registering Listeners...")
        api.setUpdatesListener(UpdateHandler(api))
        InputThread(api).start()
        Log.log("Startup complete after " + (System.currentTimeMillis() - start) + "ms")
    }

    private fun getLogo(): String {
        val base = """${ANSIColor.RED}
 _______  _                 _______  _______  _        ______   _______ _________
(  ____ \( \      |\     /|(  ____ \(  ____ \( (    /|(  ___ \ (  ___  )\__   __/
| (    \/| (      | )   ( || (    \/| (    \/|  \  ( || (   ) )| (   ) |   ) (   
| (__    | |      | |   | || (_____ | (__    |   \ | || (__/ / | |   | |   | |   
|  __)   | |      | |   | |(_____  )|  __)   | (\ \) ||  __ (  | |   | |   | |   
| (      | |      | |   | |      ) || (      | | \   || (  \ \ | |   | |   | |   
| )      | (____/\| (___) |/\____) || (____/\| )  \  || )___) )| (___) |   | |   
|/       (_______/(_______)\_______)(_______/|/    \_)|/ \___/ (_______)   )_(   
"""
        val toReplace = ")      |"
        val replacement = ANSIColor.CYAN + version + toReplace.substring(version.length + 1).substringBefore('|') + ANSIColor.RED + '|'
        return base.replace(toReplace, ")$replacement") + ANSIColor.RESET
    }
}