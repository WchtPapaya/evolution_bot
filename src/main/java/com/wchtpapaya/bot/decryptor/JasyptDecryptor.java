package com.wchtpapaya.bot.decryptor;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JasyptDecryptor implements Decryptor {
    private static final Logger log = LoggerFactory.getLogger(JasyptDecryptor.class);
    private final int bufferSize = 1024;
    private final BasicTextEncryptor encryptor;

    public JasyptDecryptor() {
        char[] key = getKeyFromFile(System.getProperty(BOT_KEYFILE));
        encryptor = createEncryptor(key);
    }

    public JasyptDecryptor(String keyFilePath) {
        char[] key = getKeyFromFile(keyFilePath);
        encryptor = createEncryptor(key);
    }

    public JasyptDecryptor(char[] key) {
        encryptor = createEncryptor(key);
    }

    private BasicTextEncryptor createEncryptor(char[] key) {
        final BasicTextEncryptor encryptor;
        encryptor = new BasicTextEncryptor();
        encryptor.setPasswordCharArray(key);
        cleanKey(key);
        return encryptor;
    }

    private void cleanKey(char[] key) {
        Arrays.fill(key, Character.MIN_VALUE);
    }


    @Override
    public String decrypt(String encrypted) {
        return encryptor.decrypt(encrypted);
    }

    @Override
    public String encrypt(String decrypted) {
        return encryptor.encrypt(decrypted);
    }

    private char[] getKeyFromFile(String path) {
        try {
            try (Reader reader = new FileReader(path, StandardCharsets.UTF_8)) {
                char[] buffer = new char[bufferSize];
                int length = reader.read(buffer);
                if (length == -1)
                    throw new DecryptionException("Keyfile is empty or not available");
                checkKeyLength(reader, buffer);
                char[] key = new char[length];
                System.arraycopy(buffer, 0, key, 0, length);
                cleanKey(buffer);
                return key;
            }
        } catch (IOException e) {
            log.error("Can not read from file {}", path);
            throw new DecryptionException(e);
        }
    }

    private void checkKeyLength(Reader reader, char[] buffer) throws IOException {
        if (buffer.length >= bufferSize) {
            int length = reader.read(buffer);
            if (length > 0)
                throw new DecryptionException("Key is too long");
        }
    }
}
