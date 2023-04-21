package com.wchtpapaya.bot.telegram.message;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public record Reply(String answer, String replyText, boolean hiddenInHelp) {

    public static void saveCollectionTo(String jsonFile, Collection<Reply> replyTexts) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(jsonFile), replyTexts);
    }

    public static Reply findReplyByAnswer(Collection<Reply> replies, String answer) {
        for (Reply r : replies) {
            if (r.answer().equals(answer)) return r;
        }
        return null;
    }

    public static Reply[] loadArrayFrom(String jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonFile), Reply[].class);
    }
}
