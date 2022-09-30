package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class GuildUnsubscribeCommand extends AbstractCommand {
    public GuildUnsubscribeCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (!guildInfo.subscribedToGuild()) {
            sendMessage(event, "Хм, а я и не подписан..");
        } else {
            guildInfo.setGuildID(null);
            sendMessage(event, "Больше не привязан к этой гильдии, свобода!");
            updateGuildInfoFile();
        }
    }
}
