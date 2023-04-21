package com.wchtpapaya.bot.decryptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JasyptDecryptorTest {

    static final String KEY = "12example_key$&";
    static final String VALUE = "example_value";
    static final String ENC_VALUE = "u2oyz5aOuinZVhniGyWYHYy/HWv+FHjc";
    static final String KEYFILE = "com/wchtpapaya/bot/decryptor/test_key.txt";
    static final String EMPTY_KEYFILE = "com/wchtpapaya/bot/decryptor/test_empty_key.txt";
    static final String LONG_KEYFILE = "com/wchtpapaya/bot/decryptor/test_long_key.txt";
    private static final String KEYFILE_PATH;
    private static final String EMPTY_KEYFILE_PATH;
    private static final String LONG_KEYFILE_PATH;

    static {
        ClassLoader loader = JasyptDecryptor.class.getClassLoader();
        KEYFILE_PATH = loader.getResource(KEYFILE).getPath();
        EMPTY_KEYFILE_PATH = loader.getResource(EMPTY_KEYFILE).getPath();
        LONG_KEYFILE_PATH = loader.getResource(LONG_KEYFILE).getPath();
    }

    @Test
    void testDecryptWithEncryptedKey() {
        Decryptor decryptor = new JasyptDecryptor(KEY.toCharArray());
        String decValue = decryptor.decrypt(ENC_VALUE);

        Assertions.assertEquals(VALUE, decValue);
    }

    @Test
    void testEncryptWithEncryptedKey() {
        Decryptor decryptor = new JasyptDecryptor(KEY.toCharArray());
        String encValue = decryptor.encrypt(VALUE);
        String decValue = decryptor.decrypt(encValue);

        Assertions.assertEquals(VALUE, decValue);
    }

    @Test
    void testKeyLoadFromFile() {
        Decryptor decryptor = new JasyptDecryptor(KEYFILE_PATH);
        String decValue = decryptor.decrypt(ENC_VALUE);

        Assertions.assertEquals(VALUE, decValue);
    }

    @Test
    void testKeyLoadFromSysVariable() {
        System.setProperty(Decryptor.BOT_KEYFILE, KEYFILE_PATH);

        Decryptor decryptor = Decryptor.instance();
        String decValue = decryptor.decrypt(ENC_VALUE);

        Assertions.assertEquals(VALUE, decValue);
    }

    @Test
    void testEmptyKeyFromFile() {
        Assertions.assertThrows(DecryptionException.class, () -> new JasyptDecryptor(EMPTY_KEYFILE_PATH));
    }

    @Test
    void testLongKey() {
        Assertions.assertThrows(DecryptionException.class, () -> new JasyptDecryptor(LONG_KEYFILE_PATH));
    }
}
