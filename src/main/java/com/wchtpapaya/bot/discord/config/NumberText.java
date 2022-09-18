package com.wchtpapaya.bot.discord.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NumberText {
    private final Map<Integer, String> texts;

    public static NumberText loadFrom(String jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonFile), NumberText.class);
    }

    public NumberText() {
        texts = new HashMap<>();
    }

    public NumberText(Map<Integer, String> texts) {
        this.texts = texts;
    }

    public String get(Integer num) {
        return texts.get(num);
    }
}
