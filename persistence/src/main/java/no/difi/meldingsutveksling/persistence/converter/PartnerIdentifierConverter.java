package no.difi.meldingsutveksling.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;

@Converter(autoApply = true)
public class PartnerIdentifierConverter implements AttributeConverter<PartnerIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(PartnerIdentifier attribute) {
        return attribute != null ? attribute.getDatabaseValue() : null;
    }

    @Override
    public PartnerIdentifier convertToEntityAttribute(String dbData) {
        return dbData != null ? PartnerIdentifier.parseDatabaseValue(dbData) : null;
    }
}
