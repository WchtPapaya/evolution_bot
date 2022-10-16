package com.wchtpapaya.bot;

import lombok.Getter;

import java.time.LocalTime;
import java.util.HashMap;

@Getter
public class CallInfo {
    private LocalTime callTime;
    private HashMap<Long, Integer> telegramMessagesInfo;

    public CallInfo(LocalTime callTime, HashMap<Long, Integer> telegramMessagesInfo) {
        this.callTime = callTime;
        this.telegramMessagesInfo = telegramMessagesInfo;
    }
}
