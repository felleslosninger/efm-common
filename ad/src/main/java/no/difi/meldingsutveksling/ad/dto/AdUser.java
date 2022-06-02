package no.difi.meldingsutveksling.ad.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdUser {
    private List<SecurityGroup> access;
    private String name;
}
