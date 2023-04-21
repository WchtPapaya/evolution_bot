package com.wchtpapaya.bot;

import com.wchtpapaya.bot.decryptor.Decryptor;
import com.wchtpapaya.bot.discord.DiscordEvolutionBot;
import com.wchtpapaya.bot.telegram.connector.TelegramConnector;

import java.util.Properties;

import static com.wchtpapaya.bot.Utils.getProperties;


public class App {
    public static final String CONFIG_PROPERTIES = "bot.properties";
    private static final String DISCORD_TOKEN_PROP = "dis.token";

    public static void main(String[] args) {
        Properties props = getProperties();

        Decryptor decryptor = Decryptor.instance();
        DiscordEvolutionBot dscbot = new DiscordEvolutionBot(TelegramConnector.instance());
        String token = decryptor.decrypt(props.getProperty(DISCORD_TOKEN_PROP));
        dscbot.start(token);
    }

}
