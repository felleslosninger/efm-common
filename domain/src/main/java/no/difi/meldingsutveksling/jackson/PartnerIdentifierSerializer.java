package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;

import java.io.IOException;

@RequiredArgsConstructor
public class PartnerIdentifierSerializer<T extends PartnerIdentifier> extends JsonSerializer<T> {

    private final Class<T> handledType;

    @Override
    public Class<T> handledType() {
        return handledType;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value != null ? value.toString() : null);
    }
}
