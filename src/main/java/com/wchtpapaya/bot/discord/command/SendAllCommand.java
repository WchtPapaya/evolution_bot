package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.telegram.connector.TelegramConnector;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SendAllCommand extends AbstractCommand {
    private static final Logger log = LoggerFactory.getLogger(SendAllCommand.class);

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
