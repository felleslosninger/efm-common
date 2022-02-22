package no.difi.meldingsutveksling.spring.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Bean
    public Iso6523Converter iso6523Converter() {
        return new Iso6523Converter();
    }
}
