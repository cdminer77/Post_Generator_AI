# 📖 Manuale Utente: Post Generator AI (Spring Boot & Google Gemini)

Benvenuto nel **Manuale Utente** di **Post Generator AI**, l'applicazione web per l'analisi intelligente di siti web aziendali, l'estrazione dell'oggetto sociale e la generazione automatizzata di post Facebook con immagini coordinate ad alta risoluzione.

---

## 📑 Indice dei Contenuti
1. [Panoramica dell'Applicazione](#1-panoramica-dellapplicazione)
2. [Requisiti di Sistema](#2-requisiti-di-sistema)
3. [Guida all'Installazione e Configurazione](#3-guida-allinstallazione-e-configurazione)
4. [Come Avviare l'Applicazione in Locale](#4-come-avviare-lapplicazione-in-locale)
5. [Come Utilizzare l'Interfaccia Web](#5-come-utilizzare-linterfaccia-web)
6. [Personalizzazione Avanzata & Porte](#6-personalizzazione-avanzata--porte)
7. [Risoluzione dei Problemi (Troubleshooting)](#7-risoluzione-dei-problemi-troubleshooting)

---

## 1. Panoramica dell'Applicazione

### Flusso di Lavoro
1. **Analisi Sito**: il crawler analizza la Home Page ed esplora le sotto-pagine istituzionali (*Chi siamo*, *About*, *Servizi*), ripulendo il codice da pubblicità e cookie policy.
2. **Estrazione con AI**: Google Gemini sintetizza l'attività core dell'azienda, la USP e il pubblico target.
3. **Copywriting Specializzato**: viene redatto un post persuasivo con hook iniziale, spaziatura con emoji, call to action e hashtag mirati.
4. **Grafica Coordinata**: il sistema elabora un prompt visivo e genera un'immagine ottimizzata per Facebook pronta al download.

---

## 2. Requisiti di Sistema

Prima di avviare il progetto, verifica che sul computer siano presenti:

| Componente | Requisito Minimo | Note |
| :--- | :--- | :--- |
| **Java (JDK)** | Versione 17 o 21 (LTS consigliata) | Verifica con `java -version` |
| **Maven** | *Non richiesto* | Il wrapper `mvnw.cmd` è già incluso nel progetto |
| **Browser Web** | Chrome, Edge, Firefox, Safari | Qualsiasi browser moderno |
| **Connessione Internet** | Attiva | Necessaria per scraping e chiamate API Gemini |
| **Google Gemini API Key** | Gratuita | Ottenibile su [Google AI Studio](https://aistudio.google.com/) |

---

## 3. Guida all'Installazione e Configurazione

### Passaggio 1: Recupera il Progetto
Se non lo hai già sul tuo computer, puoi clonarlo direttamente da GitHub:
```bash
git clone https://github.com/cdminer77/Post_Generator_AI.git
cd Post_Generator_AI
```
*Se stai già lavorando nella cartella locale, entra semplicemente in:*
`C:\Users\Utente\.gemini\antigravity\scratch\fb-post-generator-ai`

---

### Passaggio 2: Configurazione della Chiave API di Gemini
Nella cartella principale del progetto è presente il file di esempio `.env.example`.

1. Crea o modifica il file `.env` nella radice del progetto:
   ```properties
   GEMINI_API_KEY=la_tua_chiave_api_qui
   GEMINI_MODEL=gemini-3.8-flash
   ```
2. Salva il file. 

> [!IMPORTANT]
> Il file `.env` è inserito nel `.gitignore`: non verrà mai caricato su repository pubblici, garantendo la totale sicurezza delle tue credenziali.

---

## 4. Come Avviare l'Applicazione in Locale

### Avvio su Windows (PowerShell o CMD)
1. Apri una finestra di **PowerShell** o del **Prompt dei comandi**.
2. Raggiungi la cartella del progetto:
   ```powershell
   cd C:\Users\Utente\.gemini\antigravity\scratch\fb-post-generator-ai
   ```
3. Lancia il server tramite il Maven Wrapper:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

### Avvio su macOS / Linux (Terminale)
1. Apri il terminale ed entra nella directory:
   ```bash
   cd Post_Generator_AI
   ```
2. Rendi eseguibile lo script (solo la prima volta) e avvia:
   ```bash
   chmod +x mvnw
   ./mvnw spring-boot:run
   ```

---

### Verifica del Corretto Avvio
Nel terminale vedrai i log di Spring Boot. Quando visualizzi le seguenti righe:
```text
Tomcat started on port 8080 (http) with context path '/'
Started FbPostGeneratorApplication in ... seconds
```
Significa che il server è pronto!

👉 **Apri il browser all'indirizzo**: **[http://localhost:8080](http://localhost:8080)**

---

## 5. Come Utilizzare l'Interfaccia Web

1. **Inserisci l'URL**: Digita l'indirizzo del sito web da analizzare (es. `https://www.enel.com`).
2. **Scegli il Tono di Voce**:
   - *Professionale & Coinvolgente* (standard di settore)
   - *Istituzionale & Autorevole* (comunicati corporate)
   - *Storytelling & Emozionale* (storia e valori del brand)
   - *Commerciale & Promozionale* (focus su vendita e lead)
   - *Fresco, Smart & Amichevole* (target giovane/B2C)
3. **Seleziona le Dimensioni dell'Immagine**:
   - **Quadrato (1080 x 1080 px)**: ideale per il feed di Facebook, Instagram e smartphone.
   - **Orizzontale (1200 x 630 px)**: formato standard link post Facebook.
4. **Clicca su "Analizza Sito & Genera Post"**:
   - In pochi secondi vedrai apparire la card con il post compilato e l'immagine coordinata.
5. **Copia & Condivisione**:
   - Clicca su **"Copia Testo"** per copiare istantaneamente il copy negli appunti.
   - Clicca su **"Scarica"** per salvare l'immagine generata in alta definizione.

---

## 6. Personalizzazione Avanzata & Porte

### Come cambiare la porta del server (se la 8080 è occupata)
Puoi avviare l'applicazione su una porta alternativa (es. `9090`):
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```
L'app sarà quindi accessibile su `http://localhost:9090`.

### Come cambiare il modello di Gemini
Nel file `.env` puoi cambiare versione in qualunque momento:
```properties
GEMINI_MODEL=gemini-3.8-flash
```

---

## 7. Risoluzione dei Problemi (Troubleshooting)

### Q1: Errore `Address already in use: bind` (Porta 8080 occupata)
- **Causa**: Un'altra istanza o programma sta usando la porta 8080.
- **Soluzione rapida (PowerShell)**:
  ```powershell
  # Trova e chiudi il processo su porta 8080
  Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
  ```
  Oppure avvia su un'altra porta come indicato nella sezione 6.

### Q2: Errore `(401) Non autorizzato` o `(400) Richiesta non valida`
- **Causa**: La chiave API di Gemini nel file `.env` contiene spazi o non è corretta.
- **Soluzione**: Apri `.env` e assicurati che non vi siano spazi vuoti prima o dopo la chiave.

### Q3: Il sito aziendale mostra blocco 403 / Cloudflare
- **Causa**: Il sito target blocca i crawler non convenzionali.
- **Soluzione**: L'applicazione dispone già di un sistema di fallback automatico: se lo scraping è bloccato, Gemini userà il dominio e la propria intelligenza contestuale per ricostruire l'oggetto sociale senza interrompere il flusso.

---

## 🛑 Come Fermare l'Applicazione
Nel terminale in cui è attivo Spring Boot, premi semplicemente la combinazione di tasti:
`CTRL + C`
*(e conferma con `S` o `Y` se richiesto dal prompt).*
