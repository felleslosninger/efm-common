package no.difi.move.common.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class JwtTokenResponse {

    private String accessToken;
    private Integer expiresIn;
    private String scope;

}
