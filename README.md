# DIFI Move Commons 
Contains modules used by more than one DIFI Move project.

## Breaking Changes i v 2.x.y
- Modulen `security` har ikke lenger støtte for den eldre (og deprecated) Spring Security 2.5.x.
  Det betyr i praksis at `Oauth2JwtAccessTokenProvider` er fjernet fra modulen og klienter som baserte seg på denne  
  må skrive om til nyere Spring Security og f.eks. benytte [JwtTokenClient.java](security/src/main/java/no/difi/move/common/oauth/JwtTokenClient.java)
  direkte selv.

- Modulen `ad` har blitt fjernet, den har berre vore i bruk i Organization-registry og move-admin,
  men er blitt ertsatta av  midlertidig hardkoda innloggingsfunksjonalitet. 
  Finst ein oppgåve på å erstatte pålogging med Entra ID : https://digdir.atlassian.net/browse/MOVE-2184

- Modulen `spring-converter` ble besluttet fjernet, den har ikke blitt tatt i bruk og det er andre varianter lokalt i applikasjonene.
  Søk etter `Iso6523Converter` på github ga heller ingen indikjasjon på at den var i bruk.

- Modulen `cloud` ble  til `Spring Cloud 2024.0.0`, men modulen kan vurders fjernet (usikker på om / hvor den benyttes).
  Spring Cloud Security står på lista over teknologi som skal fjernes.

## Andre POTENSIELLE endringer i v2.x.y (work in progress)
- 🚧 Generell oppgradering av alle avhengigheter og plugins
- 🚧 Endret fra `org.codehaus.mojo:jaxb2-maven-plugin` til YYY
- 🚧 Endret fra tomakehurst jre8 wiremock til nyeste offisielle

## Fremtidige endringer for `certvalidator` (ikke løst pr februar 2025)
- 🚧 Erstatte [klakegg ocsp](https://github.com/klakegg/pkix-ocsp), den er ikke oppdatert på mange år
- 🚧 Evaluer om vi trenger service loader biblioteket (det er dårlig vedlikeholdt https://github.com/kohsuke/metainf-services)

## Bygge alle moduler lokalt
Testet og bygget med OpenJDK 21.0.5 og Maven 3.9.9.

```bash
mvn clean package
```

For å signere artifacts med gpg bruk profil `ossrh` :
```bash
mvn clean install -Possrh
```
