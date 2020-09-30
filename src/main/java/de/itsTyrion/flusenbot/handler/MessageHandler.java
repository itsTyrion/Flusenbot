package de.itsTyrion.flusenbot.handler;import com.pengrad.telegrambot.TelegramBot;import com.pengrad.telegrambot.model.Message;import com.pengrad.telegrambot.model.User;import com.pengrad.telegrambot.model.request.InlineKeyboardButton;import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;import com.pengrad.telegrambot.model.request.Keyboard;import com.pengrad.telegrambot.model.request.ParseMode;import com.pengrad.telegrambot.request.*;import de.itsTyrion.flusenbot.command.CommandHandler;import de.itsTyrion.flusenbot.util.Log;import de.itsTyrion.flusenbot.util.NewMember;import de.itsTyrion.flusenbot.util.Utils;import lombok.NonNull;import lombok.val;import java.util.HashSet;import java.util.Random;import java.util.Set;public final class MessageHandler {    private static final Random rand = new Random();    private static final Set<Long> cooldown = new HashSet<>();    private final Keyboard board = new InlineKeyboardMarkup(//<editor-fold desc="Button arrays for the captcha keyboard">            // There's emotes in the strings (The IDE might not render them!)            new InlineKeyboardButton[]{                    new InlineKeyboardButton("😃").callbackData("1"),                    new InlineKeyboardButton("😁").callbackData("2"),                    new InlineKeyboardButton("🙃").callbackData("3")            },            new InlineKeyboardButton[]{                    new InlineKeyboardButton("😉").callbackData("4"),                    new InlineKeyboardButton("😗").callbackData("5"),                    new InlineKeyboardButton("😌").callbackData("6")            },            new InlineKeyboardButton[]{                    new InlineKeyboardButton("😜").callbackData("7"),                    new InlineKeyboardButton("😛").callbackData("8"),                    new InlineKeyboardButton("🤔").callbackData("9")            }            //</editor-fold>    );    private final TelegramBot bot;    private final CommandHandler commandHandler;    MessageHandler(TelegramBot bot) {        this.bot = bot;        commandHandler = new CommandHandler(bot);    }    public static void addCooldown(long chatID) {        cooldown.add(chatID);    }    public static void removeCooldown(long chatID) {        cooldown.add(chatID);    }    void handleMessage(@NonNull Message msg) {        if (msg.text() != null) {            if (msg.text().startsWith("/"))                commandHandler.handleCommand(msg, msg.chat(), msg.from());        } else if (msg.newChatMembers() != null) {            if (!cooldown.contains(msg.chat().id())) {                for (val user : msg.newChatMembers())                    onMemberJoin(msg, user, msg.from().id().equals(user.id()));            }        }    }    private void onMemberJoin(Message msg, User user, boolean captcha) {        if (captcha) Log.log("Join: " + user.firstName() + "; " + "sending captcha...");        else Log.log("Added: " + user.firstName() + "; " + "skipping captcha.");        val chatID = msg.chat().id();        if (!captcha)            return;        // mute new members until they pass the anti bot        bot.execute(new RestrictChatMember(chatID, user.id()).untilDate(Utils.getUnixTime() + 31));        val button = rand.nextInt(9) + 1; // get a random button, there's 9 and Telegram counts from 1 :rolleyes:        val idText = positionIntToDescription(button); // get the user friendly button description        val request = new SendMessage(chatID, String.format(GREETING, user.firstName(), user.id(), idText))                .parseMode(ParseMode.Markdown)                .replyToMessageId(msg.messageId())                .replyMarkup(board);        val id = bot.execute(request).message().messageId();        UpdateHandler.map.put(user.id(), NewMember.of(id, "" + button)); // store the expected result        Utils.runDelayed(() -> { // If there's not a been a correct input after 30 seconds, bye.            if (UpdateHandler.map.containsKey(user.id()) && !cooldown.contains(chatID)) {                // I've never seen a bot rejoin. Also prevents users being false banned for i.e. Telegram crashing                bot.execute(new KickChatMember(chatID, user.id()).untilDate(Utils.getUnixTime() + 120));                bot.execute(new DeleteMessage(chatID, id)); // Delete the welcome message//                    api.execute(new SendAnimation(chatID, "CgADAgAD2gIAAomSOUoNB6kOgnZtlwI"));            }        }, 31);    }    private static final String GREETING = "@[%s](tg://user?id=%d)Bitte wähle das %s Emoji zum verifizieren. \n" +            "Du hast 30 Sek. Zeit. 2x Fehleingabe/Zeitüberschreitung = Tschüss (Anti Bot)";    private static String positionIntToDescription(int pos) {        switch (pos) {            case 1:                return "obere, linke";            case 2:                return "obere, mittige";            case 3:                return "obere, rechte";            case 4:                return "mittlere, linke";            case 5:                return "mittlere";            case 6:                return "mittlere, rechte";            case 7:                return "untere, linke";            case 8:                return "untere, mittige";            case 9:                return "untere, rechte";            default:                throw new AssertionError();        }    }}