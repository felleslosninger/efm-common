# Changelog

## 2.x.y

### Breaking Changes i v 2.x.y
- Modulen `security` har ikke lenger støtte for den eldre (og deprecated) Spring Security 2.5.x.
  Det betyr i praksis at `Oauth2JwtAccessTokenProvider` er fjernet fra modulen og klienter som baserte seg på denne  
  må skrive om til nyere Spring Security og f.eks. benytte [JwtTokenClient.java](security/src/main/java/no/difi/move/common/oauth/JwtTokenClient.java)
  direkte selv.

- Modulen `ad` har blitt fjernet, den har berre vore i bruk i Organization-registry og move-admin,
  men er blitt ertsatta av  midlertidig hardkoda innloggingsfunksjonalitet.
  Finst ein oppgåve på å erstatte pålogging med Entra ID : https://digdir.atlassian.net/browse/MOVE-2184

- Modulen `spring-converter` ble besluttet fjernet, den har ikke blitt tatt i bruk og det er andre varianter lokalt i applikasjonene.
  Søk etter `Iso6523Converter` på github ga heller ingen indikjasjon på at den var i bruk.

- Modulen `cloud` ble besluttet fjernet, Spring Cloud Config står på lista over "teknologi" vi migrere vekk fra.

### Andre endringer i v2.x.y
- ✅ Generell oppgradering av alle avhengigheter og plugins
- ✅ Byttet fra `org.codehaus.mojo:jaxb2-maven-plugin` til `org.jvnet.jaxb:jaxb-maven-plugin` for XML Schema kodegenerering
- ✅ Endret fra tomakehurst-jre8 wiremock til nyeste offisielle fra org.wiremock
