package de.itsTyrion.flusenbot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;
import de.itsTyrion.flusenbot.util.ANSIColor;
import de.itsTyrion.flusenbot.util.NewMember;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static de.itsTyrion.flusenbot.Flusenbot.log;

/**
 * @author itsTyrion
 * @since 26.04.2019
 */
public class UpdateListener implements UpdatesListener {
    private final TelegramBot bot;
    private final Map<Integer, NewMember> map = new HashMap<>();
    private final Set<Long> cooldown = new HashSet<>();
    private final Keyboard board = new InlineKeyboardMarkup(
            //<editor-fold desc="Button arrays for the captcha keyboard">
            // There's emotes in the strings, don't touch if the IDE doesn't render!
            new InlineKeyboardButton[]{
                    new InlineKeyboardButton("😃").callbackData("1"),
                    new InlineKeyboardButton("😁").callbackData("2"),
                    new InlineKeyboardButton("🙃").callbackData("3")
            }, new InlineKeyboardButton[]{
            new InlineKeyboardButton("😉").callbackData("4"),
            new InlineKeyboardButton("😗").callbackData("5"),
            new InlineKeyboardButton("😌").callbackData("6")
    }, new InlineKeyboardButton[]{
            new InlineKeyboardButton("😜").callbackData("7"),
            new InlineKeyboardButton("😛").callbackData("8"),
            new InlineKeyboardButton("🤔").callbackData("9")
    }
            //</editor-fold>
    );

    private final ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(10);

    UpdateListener(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public int process(List<Update> updates) {
        try {
            for (val update : updates) {
                if (update.callbackQuery() != null) {
                    val query = update.callbackQuery();
                    val userID = query.from().id();
                    val entry = map.get(userID);
                    if (entry == null) continue;
                    val chatID = query.message().chat().id();
//                    if (chatID.equals(Flusenbot.ID_FLUSENALLEE)) return update.updateId();

                    if (entry.getId().equals(query.message().messageId())) {
                        if (entry.getExpectedInput().equals(query.data())) {
                            map.remove(userID);
                            bot.execute(new EditMessageText(chatID, entry.getId(), "Willkommen, " + query.from().firstName() + "^^"));
                        } else if (entry.isFirstTry()) {
                            entry.setFirstTry(false);
                        } else {
                            bot.execute(new KickChatMember(chatID, userID));
                            map.remove(userID);
                            bot.execute(new DeleteMessage(chatID, entry.getId()));
//                            bot.execute(new SendAnimation(chatID, "CgADAgAD2gIAAomSOUoNB6kOgnZtlwI"));
                        }
                    }
                } else if (update.message() != null) {
                    val msg = update.message();
                    if (msg.text() != null) {
                        val user = msg.from();
                        val chatID = msg.chat().id();
                        System.out.println(chatID);

                        if (msg.text().equals("!cooldown")) {
                            bot.execute(new GetChatAdministrators(chatID), new CooldownCallback(user.id(), chatID));
                        }
                    } else if (msg.newChatMembers() != null) {
                        if (cooldown.contains(msg.chat().id())) continue;
                        for (val usr : msg.newChatMembers()) {
                            onMemberJoin(msg, usr, msg.from().firstName().equals(usr.firstName()));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CONFIRMED_UPDATES_ALL;
    }

    private void onMemberJoin(Message msg, User user, boolean needsCaptcha) {
        log("Join: " + user.firstName() + ' ' + (needsCaptcha ? "sending captcha..." : "skipping"));
        val chatID = msg.chat().id();
        if (user.lastName() == null || user.lastName().isEmpty()) { // bots usually don't have one ;)
            // we don't need all the photos if it's a real user
            val photoRequest = new GetUserProfilePhotos(user.id()).limit(2);
            // make the request to Telegram and get the result
            val photos = bot.execute(photoRequest).photos();
            if (photos.totalCount() == 0) { // no profile photos or last name, might be a bot
                if (user.username() == null || user.username().isEmpty()) {
                    log(ANSIColor.CYAN_BG + "Highly suspicious account joined (No last name/pic/username)", Level.WARNING);
                } else
                    log("Suspicous account joined (No last name, no pic)", Level.WARNING);
            }

//            if (!Flusenbot.ID_FLUSENALLEE.equals(chatID) && needsCaptcha) {
            if (needsCaptcha) {
                val request = new SendMessage(chatID,
                        "Bitte wähle das untere, mittige Emoji zum verifizieren. \n" +
                                "Du hast 20 Sek., mehrfache Fehleingabe/Überschreitung=Tschüss (Anti Bot)" +
                                "@[" + user.firstName() + "](tg://user?id=" + user.id() + ")")
                        .parseMode(ParseMode.Markdown)
                        .replyToMessageId(msg.messageId()).replyMarkup(board);
                val id = bot.execute(request).message().messageId();
                map.put(user.id(), NewMember.of(id, "8")); // make it easy to randomize the position
                exec.schedule(() -> {
                    if (map.containsKey(user.id())) {
                        bot.execute(new KickChatMember(chatID, user.id()));
                        bot.execute(new DeleteMessage(chatID, id));
//                        bot.execute(new SendAnimation(chatID, "CgADAgAD2gIAAomSOUoNB6kOgnZtlwI"));
                    }
                }, 21, TimeUnit.SECONDS);
            }
        } else
            welcome(chatID, user);
    }

    private void welcome(Long chatID, User user) {
        bot.execute(new SendMessage(chatID, "Willkommen, " + user.firstName() + "^^"));
    }

    @RequiredArgsConstructor
    class CooldownCallback implements Callback<GetChatAdministrators, GetChatAdministratorsResponse> {
        private final int id;
        private final Long chatID;

        @Override
        public void onResponse(GetChatAdministrators request, GetChatAdministratorsResponse response) {
            if (response.administrators().stream().anyMatch(m -> m.user().id().equals(id))) {
                cooldown.add(chatID);
                bot.execute(new SendMessage(chatID, "Cooldown für 30 Sek. aktiviert! Bei Fehlern @itsTyrion nerven"));
                exec.schedule(() -> cooldown.remove(chatID), 31, TimeUnit.SECONDS);
            }
        }

        @Override
        public void onFailure(GetChatAdministrators request, IOException ex) {
            bot.execute(new SendMessage(chatID, "Ein Fehler ist aufgetreten! @itsTyrion"));
            ex.printStackTrace();
        }
    }
}