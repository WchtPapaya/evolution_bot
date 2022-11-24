package com.wchtpapaya.bot.discord.connector;

import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;

import java.util.Map;

public class TelegramSharedConnector implements TelegramConnector {

    private TelegramEvolutionBot bot;

    public TelegramSharedConnector(TelegramEvolutionBot bot) {
        this.bot = bot;
    }

    @Override
    public Map<Long, Integer> notifyListeners(String text) {
        return bot.sendToListeners(text);
    }

    @Override
    public void editMessageAtListeners(Map<Long, Integer> telegramMessagesInfo, String text) {
        bot.editMessageAtListeners(telegramMessagesInfo, text);
    }
}
