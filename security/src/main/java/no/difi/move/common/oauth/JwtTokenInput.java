package no.difi.move.common.oauth;

import lombok.Data;

import java.util.List;

@Data
public class JwtTokenInput {

    private String clientId;
    private String audience;
    private String consumerOrg;
    private List<String> scopes;
}
