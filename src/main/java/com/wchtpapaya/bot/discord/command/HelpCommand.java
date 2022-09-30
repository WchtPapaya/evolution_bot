package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.discord.config.GuildInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class HelpCommand extends AbstractCommand {

    private final Map<String, Command> commands;

    public HelpCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, Map<String, Command> commands) {
        super(guildInfo, discordClient);
        this.commands = commands;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        sendMessage(event, createHelpMessage());
        log.info("Someone asked for help from the Discord bot");
    }

    private String createHelpMessage() {
        StringBuilder message = new StringBuilder("Откликаюсь на развитый или !название_команды \n Понимаю комманды:");
        int count = 1;
        Set<String> commandNames = commands.keySet();
        for (String name : commandNames) {
            message.append("\n").append(count++).append(") ").append(name);
        }

        return message.toString().replace("_", "\\_");
    }
}
