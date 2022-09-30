package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.DiscordEvolutionBot;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
public abstract class AbstractCommand implements Command {

    protected final GuildInfo guildInfo;
    protected GatewayDiscordClient discordClient;

    public AbstractCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        this.guildInfo = guildInfo;
        this.discordClient = discordClient;
    }

    protected boolean hasCorrectGuildInfo(MessageCreateEvent event) {
        if (!guildInfo.subscribedToGuild()) {
            sendMessage(event, Messages.NO_GUILD_MESSAGE);
            return false;
        } else if (!guildInfo.subscribedToChannel()) {
            sendMessage(event, Messages.NO_CHANNEL_MESSAGE);
            return false;
        }
        return true;
    }

    protected void sendMessage(MessageCreateEvent event, String text) {
        event.getMessage()
                .getChannel().block()
                .createMessage(text).block();
        log.info("Sent the message {} ", text);
    }

    protected String getNextMessageText(String[] texts) {
        Random rand = new Random();
        return texts[rand.nextInt(texts.length)];
    }

    protected void updateGuildInfoFile() {
        try {
            guildInfo.saveTo(DiscordEvolutionBot.CONFIG_GUILD_INFO_JSON);
        } catch (IOException e) {
            log.error("Can not save a GuildInfo to a json", e);
        }
    }

    protected GuildChannel getCmdParameterFromMessage(MessageCreateEvent event, String command) {
        String content = event.getMessage().getData().content();
        int startIndex = content.indexOf(command) + command.length() + 1;
        if (startIndex > content.length() - 1) {
            sendMessage(event, Messages.NO_CHANNEL_PARAMETER);
            return null;
        }
        String channelName = content.substring(startIndex);
        List<GuildChannel> channels = discordClient.getGuildChannels(guildInfo.getGuildID()).collectList().block();

        Optional<GuildChannel> channel = channels.stream().filter(c -> c.getName().equals(channelName)).findAny();
        if (!channel.isPresent() || !(channel.get() instanceof VoiceChannel)) {
            sendMessage(event, Messages.NO_CHANNEL_FOUND);
            return null;
        }
        return channel.get();
    }

}
