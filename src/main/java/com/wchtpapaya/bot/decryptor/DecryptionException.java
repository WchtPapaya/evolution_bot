package com.wchtpapaya.bot.decryptor;

public class DecryptionException extends RuntimeException {
    public DecryptionException() {
    }

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptionException(Throwable cause) {
        super(cause);
    }

    public DecryptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
