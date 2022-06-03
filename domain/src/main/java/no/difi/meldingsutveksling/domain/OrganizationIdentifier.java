package no.difi.meldingsutveksling.domain;

public interface OrganizationIdentifier {

    String getIdentifier();

    String getQualifiedIdentifier();

    ICD getIcd();

    String getOrganizationIdentifier();

    String getOrganizationPartIdentifier();

    String getSourceIndicator();

    boolean hasSourceIndicator();
}
