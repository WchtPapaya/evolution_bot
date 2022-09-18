package com.wchtpapaya.bot.discord.extractor;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.VoiceChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface PlayersExtractor {
    Set<String> getReadyPlayers(GatewayDiscordClient discordClient, Snowflake guildId, List<Snowflake> subscribedChannelIds);
}
