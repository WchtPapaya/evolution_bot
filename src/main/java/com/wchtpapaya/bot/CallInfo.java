package com.wchtpapaya.bot;

import java.time.LocalTime;
import java.util.Map;

public record CallInfo(LocalTime callTime,
                       Map<Long, Integer> telegramMessagesInfo) {
}
