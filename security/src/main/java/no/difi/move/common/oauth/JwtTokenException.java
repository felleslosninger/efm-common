package no.difi.move.common.oauth;

public class JwtTokenException extends Exception {
    public JwtTokenException(String s, Throwable t) {
        super(s, t);
    }
}
