package com.wchtpapaya.bot.discord.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wchtpapaya.bot.discord.json.GuildInfoPojo;
import discord4j.common.util.Snowflake;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildInfo {
    private final List<Snowflake> subscribedChannelIDs;
    private final List<Snowflake> callChannelIDs;
    private Snowflake guildID;


    private List<Snowflake> admins;

    public GuildInfo() {
        subscribedChannelIDs = new ArrayList<>();
        callChannelIDs = new ArrayList<>();
    }

    public GuildInfo(GuildInfoPojo pojo) {
        guildID = Snowflake.of(pojo.guildID());
        subscribedChannelIDs = new ArrayList<>();
        for (String s : pojo.subscribedChannelIDs()) {
            subscribedChannelIDs.add(Snowflake.of(s));
        }
        admins = new ArrayList<>();
        for (String s : pojo.adminIDs()) {
            admins.add(Snowflake.of(s));
        }
        callChannelIDs = new ArrayList<>();
        for (String s : pojo.callChannelIDs()) {
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

        GuildInfoPojo pojo = new GuildInfoPojo(
                guildID.asString(),
                subscribedChannelIDs.stream().map(Snowflake::asString).toArray(String[]::new),
                admins.stream().map(Snowflake::asString).toArray(String[]::new),
                callChannelIDs.stream().map(Snowflake::asString).toArray(String[]::new));
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

    public List<Snowflake> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Snowflake> admins) {
        this.admins = admins;
    }

    public List<Snowflake> getSubscribedChannelIDs() {
        return subscribedChannelIDs;
    }

    public Snowflake getGuildID() {
        return guildID;
    }

    public List<Snowflake> getCallChannelIDs() {
        return callChannelIDs;
    }

    //TODO Maybe it is not good to use set here
    public void setGuildID(Snowflake guildID) {
        this.guildID = guildID;
    }
}
