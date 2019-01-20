# Clavardage
Application de clavardage sur un LAN de manière décentralisée ou avec un serveur de présence.

## Dépendances
### Compilation

Nous avons utiliser JAVA 8 donc modifiez votre variable d'environnement `JAVA_HOME` ainsi que votre version de JAVA par défaut en conséquence.

Nous avons aussi utiliser Maven comme gestionnaire de dépendances. Pour compiler et créer les éxécutables : 
```
mvn clean install
```

Cela vous produira 2 archives dans le dossier `target/`
* `clavardage-1.0-SNAPSHOT.jar` : utilisé pour lancer le client
* `clavardage-1.0-SNAPSHOT.war` : utilisée pour lancer le server

### Éxécution

La sauvegarde des messages se fait grâceà mongodb, il vous faudra donc lancer le daemon de mongodb avant de lancer le programme :
```
sudo systemctl start mongodb
```
ou alors 
```
sudo mongod
```

#### Tests
Pour lancer les tests `mvn test`

#### Mode décentralisé

Pour le lancer le client en mode GUI :
```
java -cp target/clavardage-1.0-SNAPSHOT.jar org.etudinsa.clavardage.GUI
```
Pour le lancer le client en mode CLI :
```
java -cp target/clavardage-1.0-SNAPSHOT.jar org.etudinsa.clavardage.CLI
```

#### Mode centralisé
Il vous faudra tout d'abord déployer `target/clavardage-1.0-SNAPSHOT.war` sur Tomcat afin de mettre en ligne le PresenceServer.
Une fois cela fait, pour le lancer le client en mode GUI :
```
java -cp target/clavardage-1.0-SNAPSHOT.jar org.etudinsa.clavardage.GUI server <ip du PresenceServer>
```
ou pour le lancer le client en mode CLI :
```
java -cp target/clavardage-1.0-SNAPSHOT.jar org.etudinsa.clavardage.CLI server <ip du PresenceServer>
```




