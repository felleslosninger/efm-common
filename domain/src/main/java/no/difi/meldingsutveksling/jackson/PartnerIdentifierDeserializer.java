package no.difi.meldingsutveksling.jackson;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.function.Function;


@RequiredArgsConstructor
public class PartnerIdentifierDeserializer<T extends PartnerIdentifier> extends ValueDeserializer<T> {

    private final Class<T> handledType;
    private final Function<String, T> parser;

    @Override
    public Class<?> handledType() {
        return handledType;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        String s = jsonParser.readValueAs(String.class);
        return s != null ? parser.apply(s) : null;
    }
}
