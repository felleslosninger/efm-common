package no.difi.meldingsutveksling.jackson;

public interface StandardBusinessDocumentType {
    String getFieldName();

    Class<?> getValueType();

    default boolean supportsEncryption() {
        return false;
    }
}
