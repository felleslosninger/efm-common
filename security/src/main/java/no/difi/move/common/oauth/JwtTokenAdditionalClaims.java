package no.difi.move.common.oauth;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JwtTokenAdditionalClaims {

    private Map<String, Object> claims = new HashMap<>();
}
