package no.difi.move.common.cert;

public class KeystoreProviderException extends Exception {

    public KeystoreProviderException(String message) {
        super(message);
    }
    public KeystoreProviderException(String s, Exception e){
        super(s, e);
    }
}
