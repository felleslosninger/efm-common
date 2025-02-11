# DIFI Move Commons 

Contains modules used by more than one DIFI Move project.


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
