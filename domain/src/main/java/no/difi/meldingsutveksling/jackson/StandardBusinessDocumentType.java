package no.difi.meldingsutveksling.jackson;

public interface StandardBusinessDocumentType {
    String getFieldName();

    <T> Class<T> getValueType();
}
