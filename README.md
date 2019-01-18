# Clavardage
Application de clavardage sur un LAN de manière décentralisée ou avec un serveur de présence.

Exécution sur un LAN: 
- lancer mongod puis mongo dans un terminal
- lancer org.etudinsa.clavardage.GUI

Exécution avec le serveur de présence:
- lancer mongod puis mongo dans un terminal
- ajouter dans la classe du org.etudinsa.clavardage.users.PresenceServer l'adresse IP du serveur
- lancer le serveur sur Tomcat
- lancer org.etudinsa.clavardage.GUI server adresse_IP_du_serveur



