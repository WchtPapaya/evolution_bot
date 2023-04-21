package com.wchtpapaya.bot.telegram.connector;

import java.util.Map;

public interface TelegramConnector {
    static TelegramConnector instance() {
        return new TelegramSharedConnector();
    }

    Map<Long, Integer> notifyListeners(String text);

    void editMessageAtListeners(Map<Long, Integer> telegramMessagesInfo, String text);
}
