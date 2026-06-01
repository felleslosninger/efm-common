package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.difi.meldingsutveksling.domain.*;

@SuppressWarnings("unused")
public class PartnerIdentifierModule extends SimpleModule {

    public PartnerIdentifierModule() {
        super(PartnerIdentifierModule.class.getSimpleName(), PackageVersion.VERSION);

        this.addSerializer(PartnerIdentifier.class, new PartnerIdentifierSerializer<>(PartnerIdentifier.class));
        this.addSerializer(Iso6523.class, new PartnerIdentifierSerializer<>(Iso6523.class));
        this.addSerializer(FiksIoIdentifier.class, new PartnerIdentifierSerializer<>(FiksIoIdentifier.class));
        this.addSerializer(PersonIdentifier.class, new PartnerIdentifierSerializer<>(PersonIdentifier.class));
        this.addDeserializer(PartnerIdentifier.class, new PartnerIdentifierDeserializer<>(PartnerIdentifier.class, PartnerIdentifier::parse));
        this.addDeserializer(Iso6523.class, new PartnerIdentifierDeserializer<>(Iso6523.class, Iso6523::parse));
        this.addDeserializer(FiksIoIdentifier.class, new PartnerIdentifierDeserializer<>(FiksIoIdentifier.class, FiksIoIdentifier::parse));
        this.addDeserializer(PersonIdentifier.class, new PartnerIdentifierDeserializer<>(PersonIdentifier.class, PersonIdentifier::parse));
        this.addDeserializer(NhnIdentifier.class, new PartnerIdentifierDeserializer<>(NhnIdentifier.class, NhnIdentifier::parse));
    }
}
