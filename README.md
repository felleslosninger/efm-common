# DIFI Move Commons 
Contains modules used by more than one DIFI Move project.

## Breaking Changes i v2 
Versjon 2.x.y har ikke lenger støtte for gammel Spring Security 2.5.x.
Det betyr i praksis at klienter som baserte seg på [Oauth2JwtAccessTokenProvider.java](security/src/main/java/no/difi/move/common/oauth/Oauth2JwtAccessTokenProvider.java)  
må skrives om til nyere Spring Security og antagelig benytte JwtTokenClient direkte. 

## Bygg prosjektet lokalt
Testet og bygget med OpenJDK 21.0.5 og Maven 3.9.9.

```bash
mvn clean package
```

For å bygge og signere med gpg bruk profil `ossrh` :
```bash
mvn clean install -Possrh
```
