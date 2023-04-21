package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelSubscribeCommand extends AbstractCommand {
 private static final Logger log = LoggerFactory.getLogger(ChannelSubscribeCommand.class);

    public ChannelSubscribeCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (!guildInfo.subscribedToGuild()) {
            sendMessage(event, Messages.NO_GUILD_MESSAGE);
            return;
        }
        GuildChannel channel = getVoiceChannelFromMessage(event);
        if (channel == null) return;

        guildInfo.addVoiceChannel(channel.getId());
        sendMessage(event, "Голосовой канал <" + channel.getName() + "> добавлен");
        log.info("Subscribed to the voice channel <{}>", channel.getName());
        updateGuildInfoFile();
    }
}
