# Post Generator AI (Spring Boot & Google Gemini)

Web application innovativa realizzata con **Spring Boot 3** e **Google Gemini** per analizzare automaticamente un sito web aziendale, estrarre l'oggetto sociale, la proposta di valore e il target di clientela, e generare un post Facebook professionale e coinvolgente corredato da un'immagine coordinata di qualità fotografica.

---

## ✨ Funzionalità

- 🌐 **Web Scraping Intelligente (Jsoup & HttpClient)**: Analisi automatica della Home Page e navigazione delle sotto-pagine aziendali (*Chi siamo*, *About*, *Servizi*) con tolleranza ai filtri anti-bot.
- 🧠 **Business Intelligence con Google Gemini**: Identificazione precisa dell'oggetto sociale, Unique Selling Proposition (USP) e target di riferimento.
- 📱 **Facebook Copywriting Specializzato**: Creazione di post ritmati con hook d'impatto, emoji contestuali, corpo chiaro, Call to Action convincente e hashtag mirati.
- 🎨 **Generazione Grafica Coordinata**: Prompt visivo cinematografico/fotografico per generare immagini nelle dimensioni ottimali per Facebook:
  - **Formato Quadrato**: 1080 x 1080 px
  - **Formato Orizzontale (Link post / Wide)**: 1200 x 630 px
- 💻 **UI Reattiva & Live Facebook Mockup**: Anteprima grafica in stile Facebook con pulsanti immediati "Copia Testo" e "Scarica Immagine".

---

## 🚀 Requisiti

- **Java**: versione 17 o 21
- **Google Gemini API Key**: ottenibile su [Google AI Studio](https://aistudio.google.com/)

---

## ⚙️ Installazione & Avvio

### 1. Clonare il repository
```bash
git clone https://github.com/cdminer77/Post_Generator_AI.git
cd Post_Generator_AI
```

### 2. Configurazione Chiave API
Crea un file `.env` nella radice del progetto (oppure copia `.env.example`):
```bash
cp .env.example .env
```
Inserisci la tua chiave API di Gemini:
```properties
GEMINI_API_KEY=la_tua_gemini_api_key
GEMINI_MODEL=gemini-3.8-flash
```

### 3. Avvio dell'Applicazione
Su Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

Su Linux / macOS:
```bash
./mvnw spring-boot:run
```

L'applicazione sarà attiva all'indirizzo:  
👉 **`http://localhost:8080/`**

---

## 🏗️ Architettura del Progetto

```
Post_Generator_AI/
├── pom.xml
├── .env.example
├── .gitignore
├── README.md
└── src/
    └── main/
        ├── java/com/example/postgenerator/
        │   ├── FbPostGeneratorApplication.java
        │   ├── controller/
        │   │   ├── PostGeneratorController.java       # Controller Web (Thymeleaf)
        │   │   └── PostApiController.java             # REST API POST /api/posts/generate
        │   ├── model/
        │   │   ├── CompanyAnalysisAndPostDto.java     # Schema JSON per Gemini
        │   │   ├── PostGenerationRequest.java
        │   │   ├── PostGenerationResponse.java
        │   │   └── ScrapedContent.java
        │   └── service/
        │       ├── WebScraperService.java             # Scraping e parsing HTML
        │       ├── SocialPostGenerationService.java   # Integrazione Google Gemini
        │       ├── ImageGenerationService.java        # Generazione visiva per FB
        │       └── PostOrchestratorService.java       # Workflow orchestrator
        └── resources/
            ├── application.yml
            └── templates/
                └── index.html                         # Mockup Card Facebook & Frontend
```
