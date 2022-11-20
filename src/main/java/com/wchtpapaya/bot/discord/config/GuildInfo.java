package com.wchtpapaya.bot.discord.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wchtpapaya.bot.discord.json.GuildInfoPojo;
import discord4j.common.util.Snowflake;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GuildInfo {
    private final List<Snowflake> subscribedChannelIDs;
    private final List<Snowflake> callChannelIDs;

    @Setter
    private Snowflake guildID;
    private List<Snowflake> admins;

    public GuildInfo() {
        subscribedChannelIDs = new ArrayList<>();
        callChannelIDs = new ArrayList<>();
    }

    public GuildInfo(GuildInfoPojo pojo) {
        guildID = Snowflake.of(pojo.getGuildID());
        subscribedChannelIDs = new ArrayList<>();
        for (String s : pojo.getSubscribedChannelIDs()) {
            subscribedChannelIDs.add(Snowflake.of(s));
        }
        admins = new ArrayList<>();
        for (String s : pojo.getAdminIDs()) {
            admins.add(Snowflake.of(s));
        }
        callChannelIDs = new ArrayList<>();
        for (String s : pojo.getCallChannelIDs()) {
            callChannelIDs.add(Snowflake.of(s));
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
        pojo.setAdminIDs(admins.stream().map(Snowflake::asString).toArray(String[]::new));
        pojo.setCallChannelIDs(callChannelIDs.stream().map(Snowflake::asString).toArray(String[]::new));
        mapper.writeValue(file, pojo);
    }

    public boolean subscribedToGuild() {
        return guildID != null && !guildID.asString().isEmpty();
    }

    public boolean subscribedToVoiceChannel() {
        return subscribedChannelIDs != null && !subscribedChannelIDs.isEmpty();
    }

    public boolean subscribed() {
        return subscribedToGuild() && subscribedToVoiceChannel();
    }

    public void addVoiceChannel(Snowflake id) {
        subscribedChannelIDs.add(id);
    }

    public boolean removeVoiceChannel(Snowflake id) {
        return subscribedChannelIDs.remove(id);
    }

    public void addTextChannel(Snowflake id) {
        callChannelIDs.add(id);
    }

    public boolean removeTextChannel(Snowflake id) {
        return callChannelIDs.remove(id);
    }
}
