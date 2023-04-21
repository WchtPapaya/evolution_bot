package com.wchtpapaya.bot.telegram;

import com.wchtpapaya.bot.Utils;
import com.wchtpapaya.bot.telegram.command.DeafCommand;
import com.wchtpapaya.bot.telegram.command.ListenCommand;
import com.wchtpapaya.bot.telegram.command.ReplyCommand;
import com.wchtpapaya.bot.telegram.message.Reply;


import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public final class TelegramEvolutionBot extends TelegramLongPollingCommandBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramEvolutionBot.class);

    public static final String CONFIG_TG_REPLIES_JSON = "config/telegram_replies.json";
    public static final String CONFIG_TG_CHATS_JSON = "data/telegram_chats.json";
    private final String name;
    private final String token;
    private final List<Long> chatsToPost;
    private final List<Reply> replyTexts;

    public TelegramEvolutionBot(String name, String token) {
        super();
        this.name = name;
        this.token = token;
        try {
            replyTexts = Arrays.asList(Reply.loadArrayFrom(CONFIG_TG_REPLIES_JSON));
            if (Files.exists(Path.of(CONFIG_TG_CHATS_JSON))) {
                List<Long> chats = new LinkedList<>(Arrays.asList(Utils.readLongArrayFromJson(CONFIG_TG_CHATS_JSON)));
                chatsToPost = Collections.synchronizedList(chats);
            } else {
                chatsToPost = Collections.synchronizedList(new LinkedList<>());
            }
        } catch (IOException e) {
            log.error("Can not read configs for telegram bot initialization: {}, {}",
                    CONFIG_TG_REPLIES_JSON,
                    CONFIG_TG_CHATS_JSON);
            throw new IllegalStateException(e);
        }
        log.info("Initialized telegram bot");
    }

    public void startAtSeparateThread() {
        registerCommands();
        Thread thread = new Thread(() -> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(this);
            } catch (TelegramApiException e) {
                log.error("Error at Telegram bot");
                throw new IllegalStateException(e);
            }
        });
        thread.start();
    }

    private void registerCommands() {
        register(new ReplyCommand("hi", "Say hello to Bot", "KEKW товарищ"));
        register(new ReplyCommand("chathelp", "List of chat commands", createReplyHelpMessage()));
        register(new ListenCommand("listen", "Bot will subscribe to chat", this));
        register(new DeafCommand("deaf", "Bot will unsubscribe from chat", this));
        register(new HelpCommand("help", "Displays all commands", "Displays all commands"));
        log.info("Registered commands for telegram bot");
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();

        String text = message.getText();
        if (text != null) {
            replyOnText(message, text);
        }
    }

    public HashMap<Long, Integer> sendToListeners(String text) {
        HashMap<Long, Integer> responses = new HashMap<>();
        for (Long id : chatsToPost) {
            SendMessage message = new SendMessage();
            message.setText(text);
            message.enableMarkdown(false);
            message.setChatId(id);
            try {
                var response = execute(message);
                responses.put(response.getChatId(), response.getMessageId());
            } catch (TelegramApiException e) {
                log.error("Error at sending message", e);
            }
        }
        return responses;
    }

    public void editMessageAtListeners(Map<Long, Integer> messages, String text) {
        for (var m : messages.entrySet()) {
            EditMessageText message = new EditMessageText();
            message.setChatId(m.getKey());
            message.setMessageId(m.getValue());
            message.enableMarkdown(false);
            message.setText(text);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error at sending message", e);
            }
        }
    }

    public void syncChatsWithFile() {
        try {
            Utils.writeLongArrayToJson(CONFIG_TG_CHATS_JSON, chatsToPost);
        } catch (IOException e) {
            log.error("Can not update the json file with subscribed chats", e);
        }
    }

    private void replyOnText(Message message, String text) {
        String lowCaseText = text.toLowerCase();
        Reply reply = Reply.findReplyByAnswer(replyTexts, lowCaseText);

        if (reply != null) {
            SendMessage answer = new SendMessage();
            answer.setText(reply.replyText());
            answer.setChatId(message.getChatId());
            replyToUser(answer);
            log.info("Replayed on an user message - {}", answer.getText());
        }
    }

    private void replyToUser(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error at sending message", e);
        }
    }

    private String createReplyHelpMessage() {
        StringBuilder builder = new StringBuilder();
        for (Reply r : replyTexts) {
            if (!r.hiddenInHelp()) {
                builder.append(r.answer());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public List<Long> getChatsToPost() {
        return chatsToPost;
    }
}
