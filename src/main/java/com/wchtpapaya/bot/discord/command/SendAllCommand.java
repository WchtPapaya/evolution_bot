package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.discord.connector.TelegramConnector;
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
 * To use this add text to update.txt or send message string after command
 * <p> Example:
 * <p> /command my message to all
 * <p> It`s required to add your Discord id manually to data/guild.json to be able to send messages
 * <p>Example:
 * <p>...
 * <p>"adminIDs": [ "Your ID" ]
 * <p>...
 * <p>
 */
@Slf4j
public class SendAllCommand extends AbstractCommand {
    private final String UPDATE_FILE = "update/update.txt";
    final private TelegramConnector telegramBot;

    public SendAllCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, TelegramConnector telegramBot) {
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
            telegramBot.notifyListeners(text);
        } else {
            sendMessage(event, Messages.ONLY_ADMINS_MESSAGE);
        }
    }
}
