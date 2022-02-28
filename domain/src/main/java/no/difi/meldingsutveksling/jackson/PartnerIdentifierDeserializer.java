package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;

import java.io.IOException;
import java.util.function.Function;


@RequiredArgsConstructor
public class PartnerIdentifierDeserializer<T extends PartnerIdentifier> extends JsonDeserializer<T> {

    private final Class<T> handledType;
    private final Function<String, T> parser;

    @Override
    public Class<?> handledType() {
        return handledType;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String s = p.readValueAs(String.class);
        return s != null ? parser.apply(s) : null;
    }
}
