package com.wchtpapaya.bot.discord.command;

import com.wchtpapaya.bot.CallInfo;
import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.Utils;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.discord.config.NumberText;
import com.wchtpapaya.bot.discord.extractor.PlayersExtractor;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Set;

@Slf4j
public class CallCommand extends AbstractCommand {
    public static final String CONFIG_NUMBERS_JSON = "config/discord_call_numbers.json";
    public static final String CONFIG_CALLS_JSON = "config/discord_calls.json";
    public static final int MAX_PLAYERS = 5;
    private final PlayersExtractor playersExtractor;
    private final NumberText numberTexts;
    private final TelegramEvolutionBot minionBot;
    private final String[] callTexts;
    private CallInfo callInfo = null;
    @Getter
    @Setter
    private boolean callStartedReply = true;


    public CallCommand(GuildInfo guildInfo, GatewayDiscordClient discordClient, PlayersExtractor playersExtractor, TelegramEvolutionBot minionBot) {
        super(guildInfo, discordClient);
        this.playersExtractor = playersExtractor;
        this.minionBot = minionBot;
        try {
            numberTexts = NumberText.loadFrom(CONFIG_NUMBERS_JSON);
            callTexts = Utils.readStringArrayFromJson(CONFIG_CALLS_JSON);

        } catch (IOException e) {
            String message = "Can not load configs from json files";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (callInfo == null) {
            sendCallMessage(event);
        } else {
            if (isGif(event.getMessage().getContent())) return;
            sendMessage(event, Messages.CALL_IS_STARTED);
        }
    }

    public void editCallMessage() {
        if (callInfo == null) {
            log.error("Call has not been started");
            return;
        }
        String text = getCallTextWithPlayersNumber(null);
        minionBot.editMessageAtListeners(callInfo.getTelegramMessagesInfo(), text);

    }


    public boolean isCallStarted() {
        return callInfo != null;
    }

    public void resetCall() {
        minionBot.editMessageAtListeners(callInfo.getTelegramMessagesInfo(), Messages.CALL_IS_ENDED);
        callInfo = null;
    }

    public boolean isGif(String str) {
        return str.startsWith("https://tenor.com") && str.contains("gif");
    }

    public int getVoiceUsers() {
        int users = 0;
        for (var channelId : guildInfo.getSubscribedChannelIDs()) {
            Flux<VoiceState> voiceStateFlux = ((VoiceChannel) discordClient.getChannelById(channelId).block()).getVoiceStates();
            users += voiceStateFlux.count().block();
        }
        return users;
    }

    private void sendCallMessage(MessageCreateEvent event) {
        if (!hasCorrectGuildInfo(event)) return;

        String text = getCallTextWithPlayersNumber(event.getMember().get());
        log.info("Someone called members to play in the LoL at Discord. Sent message to Telegram listeners");
        callInfo = new CallInfo(LocalTime.now(), minionBot.sendToListeners(text));
    }

    private String getCallTextWithPlayersNumber(Member caller) {
        Set<String> readyPlayers = playersExtractor.getReadyPlayers(discordClient, guildInfo.getGuildID(), guildInfo.getSubscribedChannelIDs());
        if (caller != null) {
            readyPlayers.add(caller.getDisplayName());
        }
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
        String text = getNextMessageText(callTexts) + playersInfo;
        return text;
    }

    private String getRequiredPlayersCountText(int size) {
        return numberTexts.get(Math.max(MAX_PLAYERS - size, 0));
    }
}
