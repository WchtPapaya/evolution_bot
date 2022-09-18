package com.wchtpapaya.bot;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class Utils {

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
}
