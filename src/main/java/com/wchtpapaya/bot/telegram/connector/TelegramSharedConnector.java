package com.wchtpapaya.bot.telegram.connector;

import com.wchtpapaya.bot.decryptor.Decryptor;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;

import java.util.Map;
import java.util.Properties;

import static com.wchtpapaya.bot.Utils.getProperties;

public class TelegramSharedConnector implements TelegramConnector {
    private static final String TG_NAME = "tg.name";
    private static final String TG_TOKEN = "tg.token";
    private static final String DEFAULT_BOTNAME = "Bot";

    private final TelegramEvolutionBot bot;

    public TelegramSharedConnector() {
        Properties props = getProperties();
        String name = props.getProperty(TG_NAME, DEFAULT_BOTNAME);
        Decryptor decryptor = Decryptor.instance();

        String token = decryptor.decrypt(props.getProperty(TG_TOKEN));
        bot = new TelegramEvolutionBot(name, token);
        bot.startAtSeparateThread();
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
