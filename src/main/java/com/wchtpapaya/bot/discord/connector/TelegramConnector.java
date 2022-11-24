package com.wchtpapaya.bot.discord.connector;

import java.util.Map;

public interface TelegramConnector {
    Map<Long, Integer> notifyListeners(String text);

    void editMessageAtListeners(Map<Long, Integer> telegramMessagesInfo, String text);
}
