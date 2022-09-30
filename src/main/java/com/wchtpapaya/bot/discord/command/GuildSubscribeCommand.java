package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuildSubscribeCommand extends AbstractCommand {
    public GuildSubscribeCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient) {
        super(guildInfo, discordClient);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        Guild guild = event.getGuild().block();
        if (guild == null) {
            sendMessage(event, Messages.NO_GUILD_FOUND);
            return;
        }
        Snowflake id = guild.getId();
        log.info("Started to listen the guild <{}> with the id {}", guild.getName(), id.asString());
        sendMessage(event, "Гильдия <" + guild.getName() + "> выбрана как основная");
        guildInfo.setGuildID(id);
        updateGuildInfoFile();
    }
}
