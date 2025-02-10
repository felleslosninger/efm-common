# DIFI Move Commons 
Contains modules used by more than one DIFI Move project.

## Breaking Changes i v 2.x.y
Har ikke lenger støtte for den eldre (og deprecated) Spring Security 2.5.x.
Det betyr i praksis at `Oauth2JwtAccessTokenProvider` er fjernet fra `security` modulen og klienter som baserte seg på denne  
må skrive seg om til nyere Spring Security og f.eks. benytte [JwtTokenClient.java](security/src/main/java/no/difi/move/common/oauth/JwtTokenClient.java) direkte.

Modulen `ad` har blitt fjernet, den har berre vore i bruk i Organization-registry og move-admin, men er blitt ertsatta av
midlertidig hardkoda innloggingsfunksjonalitet.   Finst ein oppgåve på å erstatte pålogging med Entra ID : https://digdir.atlassian.net/browse/MOVE-2184

Modulen `cloud` vurders fjernet - usikker på om noen i det hele tatt benytter den.

## Andre POTENSIELLE endringer i v2.x.y (work in progress)
- 🚧 Generell oppgradering av alle avhengigheter og plugins
- 🚧 Erstattet [klakegg ocsp](https://github.com/klakegg/pkix-ocsp) med XXX
- 🚧 Byttet fra `org.codehaus.mojo:jaxb2-maven-plugin` til YYY
- 🚧 Endret fra tomakehurst jre8 wiremock til nyeste offisielle
- 🚧 Bruker vi service loader noe sted, den er ikke vedlikeholdt lenger https://github.com/kohsuke/metainf-services 

## Bygge alle moduler lokalt
Testet og bygget med OpenJDK 21.0.5 og Maven 3.9.9.

```bash
mvn clean package
```

For å signere artifacts med gpg bruk profil `ossrh` :
```bash
mvn clean install -Possrh
```
