package com.wchtpapaya.bot.discord.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuildInfoPojo {
    private String GuildID;
    private String[] subscribedChannelIDs;
    private String[] adminIDs;
}
