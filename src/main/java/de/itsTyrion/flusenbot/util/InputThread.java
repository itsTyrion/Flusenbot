package de.itsTyrion.flusenbot.util;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Scanner;

@RequiredArgsConstructor
public final class InputThread extends Thread {
    private final TelegramBot bot;

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            try {
                String input = sc.nextLine().trim();
                var split = input.split(" ");

                //noinspection IfCanBeSwitch
                if (split[0].equals("stop")) {
                    Runtime.getRuntime().exit(0);
                } else if (split[0].equals("send")) {
                    var joined = String.join(" ", Arrays.copyOfRange(split, 2, split.length));
                    bot.execute(new SendMessage(Long.parseLong(split[1]), joined));
                } else if (split[0].equals("del")) {
                    bot.execute(new DeleteMessage(Long.parseLong(split[1]), Integer.parseInt(split[2])));
                } else if (split[0].equals("welcome"))
                    bot.execute(new SendMessage(Long.parseLong(split[1]), "Willkommen, " + split[2] + "^^"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        sc.close();
    }
}