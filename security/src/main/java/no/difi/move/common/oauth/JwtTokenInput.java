package no.difi.move.common.oauth;

import lombok.Data;

import java.time.temporal.TemporalAmount;
import java.util.List;

@Data
public class JwtTokenInput {

    private String clientId;
    private String audience;
    private List<String> scopes;
}
