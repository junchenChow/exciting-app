package me.vociegif.android.mvp.hepler.exception;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class AccountForcedException extends RuntimeException {

    public AccountForcedException() {
    }

    public AccountForcedException(String detailMessage) {
        super(detailMessage);
    }

    public AccountForcedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AccountForcedException(Throwable throwable) {
        super(throwable);
    }
}
