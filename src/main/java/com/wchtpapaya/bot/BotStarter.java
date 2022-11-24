package com.wchtpapaya.bot;

import com.wchtpapaya.bot.discord.DiscordEvolutionBot;
import com.wchtpapaya.bot.discord.connector.TelegramConnector;
import com.wchtpapaya.bot.discord.connector.TelegramRestConnector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BotStarter {
    public static void main(String[] args) {
        TelegramConnector connector = new TelegramRestConnector();
        DiscordEvolutionBot bot = new DiscordEvolutionBot(connector);
        bot.start(args[2]);
    }
}
