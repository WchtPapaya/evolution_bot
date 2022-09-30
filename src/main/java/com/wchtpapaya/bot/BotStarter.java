package com.wchtpapaya.bot;

import com.wchtpapaya.bot.discord.DiscordEvolutionBot;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class BotStarter {
    public static void main(String[] args) {
        TelegramEvolutionBot minionBot = new TelegramEvolutionBot(args[0], args[1]);

        Thread discordThread = new Thread(() -> {
            DiscordEvolutionBot bot = new DiscordEvolutionBot();
            bot.setTelegramBot(minionBot);
            bot.start(args[2]);
        });
        discordThread.start();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(minionBot);
        } catch (TelegramApiException e) {
            log.error("Something with bot", e);
        }
    }
}
