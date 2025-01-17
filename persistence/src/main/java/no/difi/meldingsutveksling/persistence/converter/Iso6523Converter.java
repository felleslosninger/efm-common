package no.difi.meldingsutveksling.persistence.converter;

import no.difi.meldingsutveksling.domain.Iso6523;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class Iso6523Converter implements AttributeConverter<Iso6523, String> {
    @Override
    public String convertToDatabaseColumn(Iso6523 attribute) {
        return attribute != null ? attribute.getIdentifier() : null;
    }

    @Override
    public Iso6523 convertToEntityAttribute(String dbData) {
        return dbData != null ? Iso6523.parse(dbData) : null;
    }
}
