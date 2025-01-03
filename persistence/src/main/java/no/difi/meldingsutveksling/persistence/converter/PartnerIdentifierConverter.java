package no.difi.meldingsutveksling.persistence.converter;

import no.difi.meldingsutveksling.domain.PartnerIdentifier;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PartnerIdentifierConverter implements AttributeConverter<PartnerIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(PartnerIdentifier attribute) {
        return attribute != null ? attribute.getIdentifier() : null;
    }

    @Override
    public PartnerIdentifier convertToEntityAttribute(String dbData) {
        return dbData != null ? PartnerIdentifier.parse(dbData) : null;
    }
}
