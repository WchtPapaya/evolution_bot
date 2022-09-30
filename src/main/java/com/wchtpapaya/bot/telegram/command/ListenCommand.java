package com.wchtpapaya.bot.telegram.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ListenCommand extends ServiceCommand {

    private TelegramEvolutionBot bot;

    public ListenCommand(String identifier, String description, TelegramEvolutionBot bot) {
        super(identifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var chatsToPost = bot.getChatsToPost();
        Long chatId = chat.getId();
        if (!chatsToPost.contains(chatId)) {
            chatsToPost.add(chatId);
            sendAnswer(absSender, chatId, Messages.TELEGRAM_SUBSCRIBE_MESSAGE);
            bot.syncChatsWithFile();
        }
    }
}
