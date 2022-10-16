package com.wchtpapaya.bot.discord.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wchtpapaya.bot.discord.json.GuildInfoPojo;
import discord4j.common.util.Snowflake;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GuildInfo {
    private final List<Snowflake> subscribedChannelIDs;
    @Setter
    private Snowflake guildID;

    public GuildInfo() {
        subscribedChannelIDs = new ArrayList<>();
    }

    public GuildInfo(GuildInfoPojo pojo) {
        guildID = Snowflake.of(pojo.getGuildID());
        subscribedChannelIDs = new ArrayList<>();
        for (String s : pojo.getSubscribedChannelIDs()) {
            subscribedChannelIDs.add(Snowflake.of(s));
        }
    }

    public static GuildInfo loadFrom(String jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GuildInfoPojo pojo = mapper.readValue(new File(jsonFile), GuildInfoPojo.class);
        return new GuildInfo(pojo);
    }

    public void saveTo(String jsonFile) throws IOException {
        File file = new File(jsonFile);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        ObjectMapper mapper = new ObjectMapper();

        GuildInfoPojo pojo = new GuildInfoPojo();
        pojo.setGuildID(guildID.asString());
        pojo.setSubscribedChannelIDs(subscribedChannelIDs.stream().map(Snowflake::asString).toArray(String[]::new));
        mapper.writeValue(file, pojo);
    }

    public boolean subscribedToGuild() {
        return guildID != null && !guildID.asString().isEmpty();
    }

    public boolean subscribedToChannel() {
        return subscribedChannelIDs != null && !subscribedChannelIDs.isEmpty();
    }

    public boolean subscribed() {
        return subscribedToGuild() && subscribedToChannel();
    }

    public void addChannel(Snowflake id) {
        subscribedChannelIDs.add(id);
    }

    public boolean removeChannel(Snowflake id) {
        return subscribedChannelIDs.remove(id);
    }
}
