package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Utils;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ReplyCommand extends AbstractCommand {
    public static final String CONFIG_REPLIES_JSON = "config/discord_replies.json";
    private final String[] replyTexts;

    public ReplyCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, String[] replyTexts) {
        super(guildInfo, discordClient);
        this.replyTexts = replyTexts;
    }

    public ReplyCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
        try {
            replyTexts = Utils.readStringArrayFromJson(CONFIG_REPLIES_JSON);

        } catch (IOException e) {
            String message = "Can not load configs from json files";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void execute(MessageCreateEvent event) {
        sendMessage(event, getNextMessageText(replyTexts));
        log.info("Someone asked the Discord bot");
    }
}
