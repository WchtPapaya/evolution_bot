package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelUnsubscribeCommand extends AbstractCommand {
    public ChannelUnsubscribeCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
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
        if (guildInfo.removeVoiceChannel(channel.getId())) {
            sendMessage(event, "Больше не слушаю этот канал");
            log.info("Unsubscribed from the channel <{}>", channel.getName());
            updateGuildInfoFile();
        } else {
            sendMessage(event, "Ложная тревога, не слушал такой канал");
        }
    }
}
