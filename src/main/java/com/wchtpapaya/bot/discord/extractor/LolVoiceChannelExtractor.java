package com.wchtpapaya.bot.discord.extractor;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.VoiceChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LolVoiceChannelExtractor implements PlayersExtractor {

    @Override
    public Set<String> getReadyPlayers(GatewayDiscordClient discordClient, Snowflake guildId, List<Snowflake> subscribedChannelIds) {
        var guild = discordClient.getGuildById(guildId).block();

        List<Member> members = getGuildMembers(guild);
        List<GuildChannel> channels = getGuildChannels(guild);

        Set<String> connectedMembers = new HashSet<>();
        channels.stream().filter(c -> subscribedChannelIds.contains(c.getId()))
                .forEach(c -> {
                            if (c instanceof VoiceChannel) {
                                for (Member m : members) {
                                    if (Boolean.TRUE.equals(((VoiceChannel) c).isMemberConnected(m.getId()).block())) {
                                        connectedMembers.add(m.getDisplayName());
                                    }
                                }
                            }
                        }
                );
        return connectedMembers;
    }

    private static List<Member> getGuildMembers(Guild guild) {
        return guild.getMembers().collectList().block();
    }

    private static List<GuildChannel> getGuildChannels(Guild guild) {
        return guild.getChannels().collectList().block();

    }
}
