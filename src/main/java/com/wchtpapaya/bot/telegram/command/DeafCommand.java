package com.wchtpapaya.bot.telegram.command;

import com.wchtpapaya.bot.Messages;
import com.wchtpapaya.bot.telegram.TelegramEvolutionBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class DeafCommand extends ServiceCommand {

    private TelegramEvolutionBot bot;

    public DeafCommand(String identifier, String description, TelegramEvolutionBot bot) {
        super(identifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var chatsToPost = bot.getChatsToPost();
        Long chatId = chat.getId();

        if (chatsToPost.contains(chatId)) {
            chatsToPost.remove(chatId);
            sendAnswer(absSender, chatId, Messages.TELEGRAM_UNSUBSCRIBE_MESSAGE);
            bot.syncChatsWithFile();
        }
    }
}
