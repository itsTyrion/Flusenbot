package de.itsTyrion.flusenbot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.KickChatMember;
import com.pengrad.telegrambot.request.RestrictChatMember;
import de.itsTyrion.flusenbot.util.NewMember;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author itsTyrion
 * @since 26.04.2019
 */
public class UpdateHandler implements UpdatesListener {
    private final TelegramBot bot;
    static final Map<Integer, NewMember> map = new HashMap<>();
    private final MessageHandler messageHandler;

    public UpdateHandler(TelegramBot bot) {
        this.bot = bot;
        messageHandler = new MessageHandler(bot);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            for (val update : updates) {
                if (update.message() != null) {
                    messageHandler.handleMessage(update.message());
                } else if (update.callbackQuery() != null) {
                    val query = update.callbackQuery();
                    val userID = query.from().id();
                    val entry = map.get(userID);
                    if (entry == null) continue;
                    val chatID = query.message().chat().id();
//                    if (chatID.equals(Flusenbot.ID_FLUSENALLEE)) return update.updateId();

                    if (entry.getId().equals(query.message().messageId())) {
                        if (entry.getExpectedInput().equals(query.data())) {
                            map.remove(userID);
                            bot.execute(new EditMessageText(chatID, entry.getId(),
                                    "Willkommen, " + query.from().firstName() + "^^"));
                            // unrestrict and give back chat-default permissions
                            bot.execute(new RestrictChatMember(chatID, userID, unrestrict));
                        } else if (entry.isFirstTry()) {
                            entry.setFirstTry(false);
                        } else {
                            bot.execute(new KickChatMember(chatID, userID));
                            map.remove(userID);
                            bot.execute(new DeleteMessage(chatID, entry.getId()));
//                            bot.execute(new SendAnimation(chatID, "CgADAgAD2gIAAomSOUoNB6kOgnZtlwI"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CONFIRMED_UPDATES_ALL;
    }

    private static final ChatPermissions unrestrict = new ChatPermissions()
            .canSendMessages(true).canSendMediaMessages(true).canSendOtherMessages(true).canAddWebPagePreviews(true)
            .canInviteUsers(true).canSendPolls(true).canChangeInfo(true).canPinMessages(true);
}