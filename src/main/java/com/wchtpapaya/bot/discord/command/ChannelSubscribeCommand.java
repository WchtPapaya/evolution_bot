package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelSubscribeCommand extends AbstractCommand {
    public ChannelSubscribeCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (!guildInfo.subscribedToGuild()) {
            sendMessage(event, Messages.NO_GUILD_MESSAGE);
            return;
        }
        GuildChannel channel = getCmdParameterFromMessage(event, "слушать_канал");
        if (channel == null) return;

        guildInfo.addChannel(channel.getId());
        sendMessage(event, "Голосовой канал <" + channel.getName() + "> добавлен");
        log.info("Subscribed to the voice channel <{}>", channel.getName());
        updateGuildInfoFile();
    }
}
