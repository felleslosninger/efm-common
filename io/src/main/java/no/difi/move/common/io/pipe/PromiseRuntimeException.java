package no.difi.move.common.io.pipe;

public class PromiseRuntimeException extends RuntimeException {

    PromiseRuntimeException(String message) {
        super(message);
    }

    PromiseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
