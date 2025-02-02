package no.difi.meldingsutveksling.persistence.converter;

import no.difi.meldingsutveksling.domain.PersonIdentifier;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PersonIdentifierConverter implements AttributeConverter<PersonIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(PersonIdentifier attribute) {
        return attribute != null ? attribute.getIdentifier() : null;
    }

    @Override
    public PersonIdentifier convertToEntityAttribute(String dbData) {
        return dbData != null ? PersonIdentifier.parse(dbData) : null;
    }
}
