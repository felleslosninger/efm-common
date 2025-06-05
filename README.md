# efm-common
Contains modules used by more than one eFormidling project.

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

- Modulen `cloud` ble besluttet fjernet, Spring Cloud Config står på lista over "teknologi" vi migrere vekk fra.

## Andre endringer i v2.x.y
- ✅ Generell oppgradering av alle avhengigheter og plugins
- ✅ Byttet fra `org.codehaus.mojo:jaxb2-maven-plugin` til `org.jvnet.jaxb:jaxb-maven-plugin` for XML Schema kodegenerering
- ✅ Endret fra tomakehurst-jre8 wiremock til nyeste offisielle fra org.wiremock

## Bygge alle moduler lokalt
Testet og bygget med OpenJDK 21.0.5 og Maven 3.9.9.

```bash
mvn clean package
```

## Release
Sjå dokumentasjon for [maven-release-plugin](https://maven.apache.org/maven-release/maven-release-plugin/) og [guide for maven-release-plugin](https://maven.apache.org/guides/mini/guide-releasing.html).

```bash
# lokalt repo må være i sync med origin/GitHub
git push

mvn release:prepare
# svar på tre spørsmål (sett tag lik release-versjon) 
# What is the release version for "efm-common"? (no.difi:move-common) 1.0: : 1.0.0
# What is SCM release tag or label for "efm-common"? (no.difi:move-common) 1.0.0: :
# What is the new development version for "efm-common"? (no.difi:move-common) 1.0.1-SNAPSHOT: :

mvn release:perform
```
