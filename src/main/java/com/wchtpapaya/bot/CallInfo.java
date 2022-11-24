package com.wchtpapaya.bot;

import lombok.Getter;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CallInfo {
    private LocalTime callTime;
    private Map<Long, Integer> telegramMessagesInfo;

    public CallInfo(LocalTime callTime, Map<Long, Integer> telegramMessagesInfo) {
        this.callTime = callTime;
        this.telegramMessagesInfo = telegramMessagesInfo;
    }
}
