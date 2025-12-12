package no.difi.meldingsutveksling.jackson;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

@RequiredArgsConstructor
public class PartnerIdentifierSerializer<T extends PartnerIdentifier> extends ValueSerializer<T> {

    private final Class<T> handledType;

    @Override
    public Class<T> handledType() {
        return handledType;
    }

    @Override
    public void serialize(T value, JsonGenerator jgen, SerializationContext context) {
        jgen.writeString(value != null ? value.toString() : null);
    }
}
