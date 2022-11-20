package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * To use this add text to update.txt
 * and add your Discord id to data/guild.json
 * Example:
 * ...
 * "adminIDs": [
 * "<Your ID>"
 * ]
 * ...
 *
 * Or send message after command
 * Example:
 * /<command> My message to all
 */
@Slf4j
public class SendAllCommand extends AbstractCommand {
    private final String UPDATE_FILE = "update/update.txt";
    private TelegramEvolutionBot telegramBot;

    public SendAllCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, TelegramEvolutionBot telegramBot) {
        super(guildInfo, discordClient);
        this.telegramBot = telegramBot;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        String content = event.getMessage().getContent();
        String text;
        if (content.contains(" ")) {
            text = content.substring(content.indexOf(' ') + 1);
        } else {
            try {
                text = Files.readString(Path.of(UPDATE_FILE));
            } catch (IOException e) {
                log.error("Can not read update file");
                throw new RuntimeException(e);
            }
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
