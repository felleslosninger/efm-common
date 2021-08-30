/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.move.common.cert.validator;

import java.util.Optional;

/**
 * Defines validation modes available as part of this package.
 *
 * @author erlend
 */
public enum Mode {

    @RecipePath("/pki/recipe-self-signed.xml")
    SELF_SIGNED,

    @RecipePath("/pki/recipe-norway-test.xml")
    TEST,

    @RecipePath("/pki/recipe-move-difiSigned.xml")
    MOVE,

    @RecipePath("/pki/recipe-norway-production.xml")
    PRODUCTION;

    /**
     * Fetches {@link Mode} by comparing name using String#equalsIgnoreCase.
     *
     * @param value Some string.
     * @return Mode if found.
     */
    public static Optional<Mode> of(String value) {
        for (Mode mode : values())
            if (mode.name().equalsIgnoreCase(value))
                return Optional.of(mode);

        return Optional.empty();
    }
}
