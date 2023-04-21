package com.wchtpapaya.bot.discord.json;

public record GuildInfoPojo(String guildID,
                            String[] subscribedChannelIDs,
                            String[] callChannelIDs,
                            String[] adminIDs)
{}
