package no.difi.meldingsutveksling.ad.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdGroupResponse {
    private List<SecurityGroup> value;
}
