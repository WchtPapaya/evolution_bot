package com.wchtpapaya.bot.discord.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TelegramRestConnector implements TelegramConnector {
    @Override
    public Map<Long, Integer> notifyListeners(String text) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String json = "{\"text\": \"" + text + "\"}";

        HttpPost httpPost = new HttpPost("http://localhost:25940/notifyListeners");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response2 = httpClient.execute(httpPost)) {
            HttpEntity entity2 = response2.getEntity();
            String response = EntityUtils.toString(entity2);

            ObjectMapper mapper = new ObjectMapper();
            MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, Long.class, Integer.class);
            return mapper.readValue(response, mapType);
        } catch (ClientProtocolException e) {
            log.error("Can not send text to Telegram listeners");
        } catch (IOException e) {
            log.error("Can not read response from telegram bot");
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            log.error("Can not close httpClient properly");
        }
        return new HashMap<>();
    }

    @Override
    public void editMessageAtListeners(Map<Long, Integer> telegramMessagesInfo, String text) {

    }
}
