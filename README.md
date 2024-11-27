# MonsterTGame

**MonsterTGame** ist ein spannendes Kartenspiel, bei dem Spieler Monsterkarten sammeln, tauschen und in epischen Kämpfen gegeneinander antreten. Das Spiel ist in Java geschrieben und nutzt Shell-Skripte für verschiedene Operationen.

---

## **Projektübersicht**

MonsterTGame bietet eine Vielzahl von Funktionen, darunter:
- **Benutzerregistrierung**
- **Login**
- **Paketverwaltung**
- **Kartensammlung**
- **Kämpfe**

Das Spiel ist serverbasiert und nutzt HTTP-Anfragen, um verschiedene Aktionen durchzuführen.

---

## **Funktionen**

### **1. Benutzerregistrierung und Login**
- **Registrierung:**  
  Neue Benutzer können sich registrieren, indem sie einen Benutzernamen und ein Passwort angeben.  
  Erfolgreiche Registrierungen geben einen **HTTP 201-Status** zurück.
- **Login:**  
  Registrierte Benutzer können sich einloggen, um einen Authentifizierungstoken zu erhalten.  
  Fehlgeschlagene Logins geben einen **HTTP 4xx-Status** zurück.

### **2. Paketverwaltung**
- **Paket erstellen:**  
  Admin-Benutzer können neue Monsterpakete erstellen.  
  Erfolgreiche Paketkreationen geben einen **HTTP 201-Status** zurück.
- **Paket erwerben:**  
  Spieler können Pakete erwerben, um neue Monsterkarten zu ihrer Sammlung hinzuzufügen.  
  Erfolgreiche Erwerbungen geben einen **HTTP 201-Status** zurück.  
  Fehlgeschlagene Erwerbungen (z. B. bei fehlendem Guthaben) geben einen **HTTP 4xx-Status** zurück.

### **3. Kartensammlung**
- **Alle Karten anzeigen:**  
  Spieler können alle ihre gesammelten Karten anzeigen lassen.  
  Diese Aktion gibt einen **HTTP 200-Status** zurück.
- **Deck konfigurieren:**  
  Spieler können ihre Decks konfigurieren, indem sie bestimmte Karten auswählen.  
  Erfolgreiche Konfigurationen geben einen **HTTP 2xx-Status** zurück.

### **4. Kämpfe**
- **Kampf starten:**  
  Spieler können gegen andere Spieler in spannenden Kämpfen antreten.  
  Kampfanforderungen geben einen **HTTP 200-Status** zurück, und die Ergebnisse werden entsprechend aktualisiert.

### **5. Handelsfunktionen**
- **Handelsangebote anzeigen:**  
  Spieler können aktuelle Handelsangebote einsehen.  
  Diese Aktion gibt einen **HTTP 200-Status** zurück.
- **Handelsangebot erstellen:**  
  Spieler können eigene Handelsangebote erstellen, um ihre Karten zu tauschen.  
  Erfolgreiche Angebote geben einen **HTTP 201-Status** zurück.
- **Handelsangebot annehmen:**  
  Spieler können Handelsangebote anderer Spieler annehmen.  
  Erfolgreiche Handelsaktionen geben einen **HTTP 201-Status** zurück.

---

## **Ablauf**

1. **Benutzerregistrierung:**  
   Neue Spieler registrieren sich mit einem Benutzernamen und einem Passwort.
2. **Login:**  
   Spieler loggen sich ein, um Zugang zu ihren Konten und Funktionen zu erhalten.
3. **Paketverwaltung:**  
   Admins erstellen Pakete, die Spieler erwerben können, um ihre Kartensammlung zu erweitern.
4. **Kartensammlung:**  
   Spieler zeigen ihre Karten an und konfigurieren ihre Decks für Kämpfe.
5. **Kämpfe:**  
   Spieler treten in Kämpfen gegeneinander an, um ihre Fähigkeiten und Strategien zu testen.
6. **Handelsfunktionen:**  
   Spieler handeln Karten, um ihre Sammlungen zu verbessern und strategische Vorteile zu erlangen.

---

## **Schlussfolgerung**

**MonsterTGame** ist ein umfassendes und unterhaltsames Kartenspiel, das eine Vielzahl von Funktionen bietet, um Spieler zu begeistern und herauszufordern. Mit einer klaren Struktur und gut definierten HTTP-Antworten sorgt es für eine reibungslose und spannende Spielerfahrung.

---

## **Anleitung zur Datenbankintegration**

### **1. Docker starten**
- Stelle sicher, dass Docker läuft, und überprüfe vorhandene Container mit:
  ```bash
  docker ps
Um den PostgreSQL-Container zu starten:
```bash

docker run --name monsterTGame \
-e POSTGRES_USER=mike \
-e POSTGRES_PASSWORD= \
-e POSTGRES_DB=mtcg \
-e POSTGRES_HOST_AUTH_METHOD=trust \
-p 5432:5432 \
-d postgres 

### **2. Verbindung zur Datenbank herstellen**

   Nutze psql, um auf die Datenbank zuzugreifen:
   bash
   Code kopieren
   psql -U mike -d mtcg -h localhost -p 5432

### **3. Datenbankbefehle ausführen**
   Um die users-Tabelle zu überprüfen:
   sql
  
   SELECT * FROM users;
   Sicherstellen, dass alle notwendigen Tabellen und Daten korrekt vorhanden sind.

### **4. Fehlerbehebung**

   a. psql nicht gefunden
   Installiere den PostgreSQL-Client oder überprüfe den Pfad in der Systemumgebung.
   b. Docker-Probleme
   Stelle sicher, dass der Container läuft:
   bash
   Code kopieren
   docker start monsterTGame
   Prüfe Logs mit:
   bash
   Code kopieren
   docker logs monsterTGame
   Code kopieren






