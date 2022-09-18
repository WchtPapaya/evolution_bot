package com.wchtpapaya.bot.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ReplyCommand extends ServiceCommand {

    private final String answer;

    public ReplyCommand(String identifier, String description, String answer) {
        super(identifier, description);
        this.answer = answer;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, chat.getId(), answer);
    }
}
