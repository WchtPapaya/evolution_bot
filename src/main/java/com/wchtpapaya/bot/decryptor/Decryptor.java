package com.wchtpapaya.bot.decryptor;

public interface Decryptor {
    String BOT_KEYFILE = "bot.keyfile";

    /**
     * Configures an instance of the Decryptor. <br>
     * Requires the system variable with
     * keyfile path - {@value Decryptor#BOT_KEYFILE} to be set.
     */
    static Decryptor instance() {
        return new JasyptDecryptor();
    }

    String decrypt(String encrypted);

    String encrypt(String decrypted);

}