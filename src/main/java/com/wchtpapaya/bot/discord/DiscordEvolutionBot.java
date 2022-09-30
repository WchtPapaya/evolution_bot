package com.wchtpapaya.bot.discord;

import com.wchtpapaya.bot.discord.command.*;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.discord.extractor.LolVoiceChannelExtractor;
import com.wchtpapaya.bot.discord.extractor.PlayersExtractor;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.gateway.intent.IntentSet;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class DiscordEvolutionBot {

    public static final String CONFIG_NUMBERS_JSON = "config/discord_call_numbers.json";
    public static final String CONFIG_REPLIES_JSON = "config/discord_replies.json";
    public static final String CONFIG_CALLS_JSON = "config/discord_calls.json";
    public static final String CONFIG_GUILD_INFO_JSON = "data/guildinfo.json";
    private final Map<String, Command> commands = new HashMap<>();

    @Setter
    private TelegramEvolutionBot telegramBot;
    private final PlayersExtractor playersExtractor = new LolVoiceChannelExtractor();

    private final GuildInfo guildInfo;
    private GatewayDiscordClient discordClient;

    public DiscordEvolutionBot() {
        try {
            if (Files.exists(Path.of(CONFIG_GUILD_INFO_JSON))) {
                guildInfo = GuildInfo.loadFrom(CONFIG_GUILD_INFO_JSON);
            } else {
                guildInfo = new GuildInfo();
            }

        } catch (IOException e) {
            String message = "Can not load configs from json files";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    public void start(String token) {

        commands.put("help", new HelpCommand(guildInfo, discordClient, commands));
        commands.put("ответь", new ReplyCommand(guildInfo, discordClient));
        commands.put("призыв", new CallCommand(guildInfo, discordClient, playersExtractor, telegramBot));
        commands.put("слушать_гильдия", new GuildSubscribeCommand(guildInfo, discordClient));
        commands.put("отписаться_гильдия", new GuildUnsubscribeCommand(guildInfo, discordClient));
        commands.put("слушать_канал", new ChannelSubscribeCommand(guildInfo, discordClient));
        commands.put("отписаться_канал", new ChannelUnsubscribeCommand(guildInfo, discordClient));
        commands.put("join", new JoinVoiceChannelCommand(guildInfo, discordClient));

        discordClient = DiscordClientBuilder.create(token).build()
                .gateway()
                .setEnabledIntents(IntentSet.all())
                .login()
                .block();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    // 3.1 Message.getContent() is a String
                    final String content = event.getMessage().getContent();

                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (checkBotPrefix(content.toLowerCase(), entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        }
                    }
                });
        discordClient.onDisconnect().block();
    }

    private boolean checkBotPrefix(String content, String commandName) {
        return content.startsWith("!" + commandName) ||
                content.startsWith("/" + commandName) ||
                content.startsWith("развитый " + commandName);
    }
}
