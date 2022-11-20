package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * To use this add text to update.txt
 * and add your Discord id to data/guild.json
 * Example:
 * ...
 * "adminIDs": [
 * "<Your ID>"
 * ]
 * ...
 */
@Slf4j
public class SendAllCommand extends AbstractCommand {
    private final String UPDATE_FILE = "config/update.txt";
    private TelegramEvolutionBot telegramBot;

    public SendAllCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, TelegramEvolutionBot telegramBot) {
        super(guildInfo, discordClient);
        this.telegramBot = telegramBot;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        String text;
        try {
            text = Files.readString(Path.of(UPDATE_FILE));
        } catch (IOException e) {
            log.error("Can not read update file");
            throw new RuntimeException(e);
        }

        Optional<Member> optionalMember = event.getMember();
        if (optionalMember.isEmpty()) {
            sendMessage(event, Messages.ADMIN_COMMAND_FROM_PRIVATE_CHAT);
            return;
        }
        Snowflake id = optionalMember.get().getId();
        if (guildInfo.getAdmins().contains(id)) {
            telegramBot.sendToListeners(text);
        } else {
            sendMessage(event, Messages.ONLY_ADMINS_MESSAGE);
        }
    }
}
