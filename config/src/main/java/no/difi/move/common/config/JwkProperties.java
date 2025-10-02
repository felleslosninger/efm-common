package no.difi.move.common.config;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class JwkProperties {

    /***
     * Path to file that contains jwk
     */
    private Resource path;
}
