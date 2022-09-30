package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;

public class JoinVoiceChannelCommand extends AbstractCommand {
    public JoinVoiceChannelCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (!hasCorrectGuildInfo(event)) return;

        VoiceChannel channel = (VoiceChannel) discordClient.getGuildById(guildInfo.getGuildID()).block()
                .getChannelById(guildInfo.getSubscribedChannelIDs().get(1)).block();
        channel.join().block();
    }
}
