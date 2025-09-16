# Efm-common

<img style="float:right" width="100" height="100" src="./EF.png" alt="Ein del av eFormidling">

## Føremål
Inneheld moduler brukt av fleire eFormidling prosjekt.

## Teknologiar i bruk
- Spring Boot

## Oppstart
### Føresetnadar
- Java 21
- Maven 3

### Bygging
```mvn clean install```

### Innstillingar

N/A

## Grensesnitt

N/A

### Web-applikasjon

N/A

### REST-API

N/A

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
