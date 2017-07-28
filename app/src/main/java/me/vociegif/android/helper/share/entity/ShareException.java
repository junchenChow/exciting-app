package me.vociegif.android.helper.share.entity;

/**
 * Created by design on 2016/3/24.
 */
public class ShareException extends RuntimeException {

    private static final long serialVersionUID = 234553784523L;

    public ShareException(String detailMessage) {
        super(detailMessage);
    }

    public ShareException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ShareException() {
        super();
    }

    public ShareException(Throwable throwable) {
        super(throwable);
    }
}