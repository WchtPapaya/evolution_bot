package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.Utils;
import com.wchtpapaya.bot.discord.DiscordEvolutionBot;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.discord.config.NumberText;
import com.wchtpapaya.bot.discord.extractor.PlayersExtractor;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class CallCommand extends AbstractCommand {
    public static final int MAX_PLAYERS = 5;

    private final PlayersExtractor playersExtractor;
    private final NumberText numberTexts;
    private TelegramEvolutionBot minionBot;

    private final String[] callTexts;

    public CallCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, PlayersExtractor playersExtractor, TelegramEvolutionBot minionBot) {
        super(guildInfo, discordClient);
        this.playersExtractor = playersExtractor;
        this.minionBot = minionBot;
        try {
            numberTexts = NumberText.loadFrom(DiscordEvolutionBot.CONFIG_NUMBERS_JSON);
            callTexts = Utils.readStringArrayFromJson(DiscordEvolutionBot.CONFIG_CALLS_JSON);

        } catch (IOException e) {
            String message = "Can not load configs from json files";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (!hasCorrectGuildInfo(event)) return;
        Set<String> readyPlayers = playersExtractor.getReadyPlayers(discordClient, guildInfo.getGuildID(), guildInfo.getSubscribedChannelIDs());

        StringBuilder playersInfo = new StringBuilder();
        playersInfo.append("\n\n");
        playersInfo.append(getRequiredPlayersCountText(readyPlayers.size()));
        if (readyPlayers.size() > 0) {
            playersInfo.append("\n\nГотовы к игре в LoL");
            playersInfo.append("\n");
            int count = 1;
            for (String player : readyPlayers) {
                playersInfo.append(count++);
                playersInfo.append(") ");
                playersInfo.append(player);
                playersInfo.append("\n");
            }
        }
        minionBot.replyToListeners(getNextMessageText(callTexts) + playersInfo.toString());
        log.info("Someone called members to play in the LoL to Telegram");
    }

    private String getRequiredPlayersCountText(int size) {
        return numberTexts.get(Math.max(MAX_PLAYERS - size, 0));
    }
}
