package no.difi.move.common.oauth;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class JwtTokenResponse {

    private String accessToken;
    private Integer expiresIn;
    private String scope;

}
