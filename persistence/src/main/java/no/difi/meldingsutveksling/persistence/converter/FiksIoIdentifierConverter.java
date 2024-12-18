package no.difi.meldingsutveksling.persistence.converter;

import no.difi.meldingsutveksling.domain.FiksIoIdentifier;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FiksIoIdentifierConverter implements AttributeConverter<FiksIoIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(FiksIoIdentifier attribute) {
        return attribute != null ? attribute.getIdentifier() : null;
    }

    @Override
    public FiksIoIdentifier convertToEntityAttribute(String dbData) {
        return dbData != null ? FiksIoIdentifier.parse(dbData) : null;
    }
}
