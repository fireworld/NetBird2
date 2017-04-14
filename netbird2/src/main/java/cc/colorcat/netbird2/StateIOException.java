package cc.colorcat.netbird2;

import java.io.IOException;

/**
 * Created by cxx on 17-3-1.
 * xx.ch@outlook.com
 */
public class StateIOException extends IOException {
    private final int state;

    public StateIOException(String detailMessage, int state) {
        super(detailMessage);
        this.state = state;
    }

    public StateIOException(String message, Throwable cause, int state) {
        super(message, cause);
        this.state = state;
    }

    public StateIOException(Throwable cause, int state) {
        super(cause);
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "msg=" + getLocalizedMessage() +
                "state=" + state +
                '}';
    }
}
