package no.difi.meldingsutveksling.spring.converter;

import no.difi.meldingsutveksling.domain.Iso6523;
import org.springframework.core.convert.converter.Converter;

public class Iso6523Converter implements Converter<String, Iso6523> {
    @Override
    public Iso6523 convert(String s) {
        return Iso6523.parse(s);
    }
}
