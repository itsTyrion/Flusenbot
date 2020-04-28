package de.itsTyrion.flusenbot.util;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Arrays;
import java.util.Scanner;

@RequiredArgsConstructor
public final class InputThread extends Thread {
    private static final long ID_FLUSENALLEE = -1001119936231L;
    private static final long ID_ATELIER = -1001238995053L;

    private final TelegramBot bot;

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            if (input.startsWith("!")) {
                val split = input.split(" ");
                val joined = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

                //noinspection IfCanBeSwitch
                if (split[0].equals("!stop")) {
                    Runtime.getRuntime().exit(0);
                } else if (split[0].equals("!fluff")) {
                    bot.execute(new SendMessage(ID_FLUSENALLEE, joined));
                } else if (split[0].equals("!atelier")) {
                    bot.execute(new SendMessage(ID_ATELIER, joined));
                } else if (split[0].equals("!delf")) {
                    bot.execute(new DeleteMessage(ID_FLUSENALLEE, Integer.parseInt(split[1])));
                } else if (split[0].equals("!dela")) {
                    bot.execute(new DeleteMessage(ID_ATELIER, Integer.parseInt(split[1])));
                } else if (split[0].equals("!wf")) {
                    bot.execute(new SendMessage(ID_FLUSENALLEE, "Willkommen, " + split[1] + "^^"));
                } else if (split[0].equals("!wa")) {
                    bot.execute(new SendMessage(ID_ATELIER, "Willkommen, " + split[1] + "^^"));
                }
            }
        }
        sc.close();
    }
}