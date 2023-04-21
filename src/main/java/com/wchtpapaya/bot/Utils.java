package com.wchtpapaya.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import static com.wchtpapaya.bot.App.CONFIG_PROPERTIES;

public class Utils {
 private static final Logger log = LoggerFactory.getLogger(Utils.class);


    public static String[] readStringArrayFromJson(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(file), String[].class);
    }

    public static Long[] readLongArrayFromJson(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(file), Long[].class);
    }

    public static void writeStringArrayToJson(String file, String[] texts) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
         mapper.writeValue(new File(file), texts);
    }

    public static void writeStringCollectionToJson(String file, Collection<String> collection) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(file), collection);
    }

    public static void writeLongArrayToJson(String file, Collection<Long> collection) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(file), collection);
    }

    public static Properties getProperties() {
        Properties props = new Properties();
        String configPath = System.getProperty(CONFIG_PROPERTIES);
        if (configPath == null) {
            log.error("Config can not be loaded from variable {}", CONFIG_PROPERTIES);
            System.exit(2);
        }
        try (FileInputStream inStream = new FileInputStream(configPath)) {
            props.load(inStream);
        } catch (IOException e) {
            log.error("Cannot load properties file: {}", configPath);
            System.exit(2);
        }
        return props;
    }

    private Utils() {
        throw new AssertionError();
    }
}
