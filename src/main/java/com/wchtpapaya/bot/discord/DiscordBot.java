package com.wchtpapaya.bot.discord;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.Utils;
import com.wchtpapaya.bot.discord.command.Command;
import com.wchtpapaya.bot.discord.config.GuildInfo;
import com.wchtpapaya.bot.discord.config.NumberText;
import com.wchtpapaya.bot.discord.extractor.LolVoiceChannelExtractor;
import com.wchtpapaya.bot.discord.extractor.PlayersExtractor;
import com.wchtpapaya.bot.telegram.TelegramMinionBot;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class DiscordBot {

    public static final int MAX_PLAYERS = 5;
    public static final String CONFIG_NUMBERS_JSON = "config/discord_call_numbers.json";
    public static final String CONFIG_REPLIES_JSON = "config/discord_replies.json";
    public static final String CONFIG_CALLS_JSON = "config/discord_calls.json";
    public static final String CONFIG_GUILD_INFO_JSON = "data/guildinfo.json";
    private final NumberText numberTexts;
    private final Map<String, Command> commands = new HashMap<>();
    private TelegramMinionBot minionBot;
    private final PlayersExtractor playersExtractor = new LolVoiceChannelExtractor();
    private final String[] replyTexts;
    private final String[] callTexts;
    private final GuildInfo guildInfo;
    private GatewayDiscordClient discordClient;

    public DiscordBot() {
        try {
            numberTexts = NumberText.loadFrom(CONFIG_NUMBERS_JSON);
            callTexts = Utils.readStringArrayFromJson(CONFIG_CALLS_JSON);
            replyTexts = Utils.readStringArrayFromJson(CONFIG_REPLIES_JSON);
            if (Files.exists(Path.of(CONFIG_GUILD_INFO_JSON))) {
                guildInfo = GuildInfo.loadFrom(CONFIG_GUILD_INFO_JSON);
            } else {
                guildInfo = new GuildInfo();
            }

        } catch (IOException e) {
            String message = "Can not load configs from json files";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    public void start(String token) {

        commands.put("help", event -> {
            sendMessage(event, createHelpMessage());
            log.info("Someone asked for help from the Discord bot");
        });

        commands.put("ответь", event -> {
            sendMessage(event, getNextMessageText(replyTexts));
            log.info("Someone asked the Discord bot");
        });

        commands.put("призыв", event -> {
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
        });

        commands.put("слушать_гильдия", event -> {
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
        });

        commands.put("отписаться_гильдия", event -> {
            if (!guildInfo.subscribedToGuild()) {
                sendMessage(event, "Хм, а я и не подписан..");
            } else {
                guildInfo.setGuildID(null);
                sendMessage(event, "Больше не привязан к этой гильдии, свобода!");
                updateGuildInfoFile();
            }
        });

        commands.put("слушать_канал", event -> {
            if (!guildInfo.subscribedToGuild()) {
                sendMessage(event, Messages.NO_GUILD_MESSAGE);
                return;
            }
            GuildChannel channel = getCmdParameterFromMessage(event, "слушать_канал");
            if (channel == null) return;

            guildInfo.addChannel(channel.getId());
            sendMessage(event, "Голосовой канал <" + channel.getName() + "> добавлен");
            log.info("Subscribed to the voice channel <{}>", channel.getName());
            updateGuildInfoFile();
        });

        commands.put("отписаться_канал", event -> {
            if (!guildInfo.subscribedToGuild()) {
                sendMessage(event, Messages.NO_GUILD_MESSAGE);
                return;
            }
            GuildChannel channel = getCmdParameterFromMessage(event, "отписаться_канал");
            if (channel == null) return;
            if (guildInfo.removeChannel(channel.getId())) {
                sendMessage(event, "Больше не слушаю этот канал");
                log.info("Unsubscribed from the channel <{}>", channel.getName());
                updateGuildInfoFile();
            } else {
                sendMessage(event, "Ложная тревога, не слушал такой канал");
            }
        });


        commands.put("join", event -> {
            if (!hasCorrectGuildInfo(event)) return;

            VoiceChannel channel = (VoiceChannel) discordClient.getGuildById(guildInfo.getGuildID()).block()
                    .getChannelById(guildInfo.getSubscribedChannelIDs().get(1)).block();
            channel.join().block();
        });

        discordClient = DiscordClientBuilder.create(token).build()
                .gateway()
                .setEnabledIntents(IntentSet.all())
                .login()
                .block();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    // 3.1 Message.getContent() is a String
                    final String content = event.getMessage().getContent();

                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (checkBotPrefix(content.toLowerCase(), entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        }
                    }
                });
        discordClient.onDisconnect().block();
    }

    private void updateGuildInfoFile() {
        try {
            guildInfo.saveTo(CONFIG_GUILD_INFO_JSON);
        } catch (IOException e) {
            log.error("Can not save a GuildInfo to a json", e);
        }
    }

    private GuildChannel getCmdParameterFromMessage(MessageCreateEvent event, String command) {
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

    private boolean hasCorrectGuildInfo(MessageCreateEvent event) {
        if (!guildInfo.subscribedToGuild()) {
            sendMessage(event, Messages.NO_GUILD_MESSAGE);
            return false;
        } else if (!guildInfo.subscribedToChannel()) {
            sendMessage(event, Messages.NO_CHANNEL_MESSAGE);
            return false;
        }
        return true;
    }

    private void sendMessage(MessageCreateEvent event, String text) {
        event.getMessage()
                .getChannel().block()
                .createMessage(text).block();
        log.info("Sent the message {} ", text);
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

    private String getRequiredPlayersCountText(int size) {
        return numberTexts.get(Math.max(MAX_PLAYERS - size, 0));
    }

    private boolean checkBotPrefix(String content, String commandName) {
        return content.startsWith("!" + commandName) ||
                content.startsWith("/" + commandName) ||
                content.startsWith("развитый " + commandName);
    }

    private String getNextMessageText(String[] texts) {
        Random rand = new Random();
        return texts[rand.nextInt(texts.length)];
    }

    public void setTelegramBot(TelegramMinionBot minionBot) {
        this.minionBot = minionBot;
    }
}
