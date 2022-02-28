package no.difi.meldingsutveksling.persistence.converter;

import no.difi.meldingsutveksling.domain.PartnerIdentifier;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
