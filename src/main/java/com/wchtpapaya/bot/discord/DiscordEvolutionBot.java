package com.wchtpapaya.bot.discord;

import com.wchtpapaya.bot.discord.command.*;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.telegram.connector.TelegramConnector;
import com.wchtpapaya.bot.discord.extractor.LolVoiceChannelExtractor;
import com.wchtpapaya.bot.discord.extractor.PlayersExtractor;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.gateway.intent.IntentSet;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordEvolutionBot {
 private static final Logger log = LoggerFactory.getLogger(DiscordEvolutionBot.class);


    public static final String CONFIG_GUILD_INFO_JSON = "data/guildinfo.json";
    private final Map<String, Command> commands = new HashMap<>();
    private final TelegramConnector tgBot;
    private final PlayersExtractor playersExtractor = new LolVoiceChannelExtractor();

    private final GuildInfo guildInfo;

    public DiscordEvolutionBot(TelegramConnector telegramConnector) {
        tgBot = telegramConnector;
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
        GatewayDiscordClient discordClient = DiscordClientBuilder.create(token).build()
                .gateway()
                .setEnabledIntents(IntentSet.all())
                .login()
                .block();

        commands.put("help", new HelpCommand(guildInfo, discordClient, commands));
        commands.put("ответь", new ReplyCommand(guildInfo, discordClient));
        CallCommand callCommand = new CallCommand(guildInfo, discordClient, playersExtractor, tgBot);
        commands.put("призыв", callCommand);
        commands.put("слушать_гильдия", new GuildSubscribeCommand(guildInfo, discordClient));
        commands.put("отписаться_гильдия", new GuildUnsubscribeCommand(guildInfo, discordClient));
        commands.put("слушать_канал", new ChannelSubscribeCommand(guildInfo, discordClient));
        commands.put("отписаться_канал", new ChannelUnsubscribeCommand(guildInfo, discordClient));
        commands.put("join", new JoinVoiceChannelCommand(guildInfo, discordClient));
        commands.put("send_all_update", new SendAllCommand(guildInfo, discordClient, tgBot));
        commands.put("set_call_chat", new SetCallChatCommand(guildInfo, discordClient));
        commands.put("remove_call_chat", new RemoveCallChatCommand(guildInfo, discordClient));

        discordClient.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    // 3.1 Message.getContent() is a String
                    final String content = event.getMessage().getContent();

                    // start a call by gifs
                    if (callCommand.isGif(content)) {
                        callCommand.execute(event);
                        return;
                    }

                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (checkBotPrefix(content.toLowerCase(), entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        }
                    }
                });
        discordClient.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(event -> {
            if (!callCommand.isCallStarted()) return;
            VoiceChannel current = event.getCurrent().getChannel().block();
            VoiceChannel old = null;
            if (event.getOld().isPresent()) {
                old = event.getOld().get().getChannel().block();
            }
            if (isVoiceChannelListened(current) || isVoiceChannelListened(old)) {
                if (callCommand.getVoiceUsers() == 0) {
                    callCommand.resetCall();
                    log.info("Call has ended, everyone has leaved from voice channels");
                    return;
                }
                callCommand.editCallMessage();
                log.info("Edited call message cause someone joined/leaved voice channel");
            }
        });

        discordClient.onDisconnect().block();
    }

    private boolean isVoiceChannelListened(VoiceChannel channel) {
        return channel != null && guildInfo.getSubscribedChannelIDs().contains(channel.getId());
    }

    private boolean checkBotPrefix(String content, String commandName) {
        return content.startsWith("!" + commandName) ||
                content.startsWith("/" + commandName) ||
                content.startsWith("развитый " + commandName);
    }
}
