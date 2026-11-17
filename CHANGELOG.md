# Changelog

## Neste versjon

### Feilrettingar
- MOVE-5430: `MarkerFactory.senderMarker` hashar no fødselsnummer (SHA-256) slik `receiverMarker` alt gjorde.
  Loggfeltet `sender_org_number` inneheld difor ein hash, ikkje fødselsnummeret, når avsendar er ein person.
  Søk i sentral logg på avsendar som er person må gjerast på hashen. Orgnummer står framleis i klårtekst.
  - `IdentifierHasher.hashIfPersonnr` tek no imot `null` og returnerer `null`, så markørane krasjar ikkje når avsendar eller mottakar manglar.

## 5.0.0

### Breaking Changes
- MOVE-5436: `security` dreg ikkje lenger inn `spring-boot-starter-webflux` (avhengigheita er no `optional`).
  - `JwtTokenClient.fetchToken()` og `generateJWT()` fungerer utan webflux. `fetchToken()` brukar no `RestClient` i staden for `RestTemplate`.
  - Appar som brukar `JwtTokenClient.fetchTokenMono()` eller `JwtWebClient` må sjølve leggje til `spring-boot-starter-webflux`
    (t.d. efm-dpv-proxy, efm-move-admin, efm-sr-proxy, og efm-integrasjonspunkt viss det ikkje alt har webflux direkte).
  - `JwtTokenClient` deklarerer framleis metodar som returnerer `Mono`. Utan reactor på classpath kan han difor ikkje
    inspiserast med refleksjon (t.d. registrerast som Spring-`@Bean` eller mockast). Lag han med `new JwtTokenClient(...)` slik ks-fiks gjer.
  - `dokumentpakking` har no `spring-boot-starter-webflux` som eigen avhengigheit, så brukarane der merkar inga endring.

## 4.0.0

### Breaking Changes
- MOVE-5376: `Iso6523` validerer strengare. Valideringa skjer no i konstruktøren og gjeld difor `parse`, `of` og alle `with`-metodane:
  - Organisasjonsidentifikatoren kan berre innehalde `A-Z`, `a-z`, `0-9`, `.`, `_` og `-` (maks 35 teikn).
    Mellomrom, kontrollteikn, `<`, `>`, `"`, `;`, `/` og andre teikn blir avviste for alle ICD-ar.
  - For ICD `0192` må organisasjonsidentifikatoren vere nøyaktig 9 siffer. `0192:fiken` og liknande blir avvist.
  - Organisasjonsdel-identifikatoren (t.d. `MP//Vergemålsetaten`) kan framleis innehalde `/` og Unicode-bokstavar,
    men ikkje mellomrom, kontroll- og formatteikn, `:`, `<`, `>`, `"`, `'`, `` ` ``, `;`, `&` eller `\`.
  - Ugyldige verdiar som alt ligg i databasar vil gi `IllegalArgumentException` når dei blir lesne via `Iso6523Converter`/`Iso6523TypeDescriptor`.
- Kontrollsifferet (modulus 11) for ICD `0192` blir **ikkje** sjekka i `parse`, slik at eksisterande data og testdata som `987654321` framleis kan lesast.
  Bruk nye `Iso6523.parseStrict` / `Iso6523.isValidStrict` når ny input skal validerast, t.d. i `@Iso6523`-validatoren.
- Feilmeldingar frå `Iso6523` og `PartnerIdentifier.parse` erstattar kontroll- og formatteikn (t.d. CR/LF) med `?`.

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
