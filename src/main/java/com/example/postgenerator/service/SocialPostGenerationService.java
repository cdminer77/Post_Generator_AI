package com.example.postgenerator.service;

import com.example.postgenerator.model.CompanyAnalysisAndPostDto;
import com.example.postgenerator.model.ScrapedContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SocialPostGenerationService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.8-flash}")
    private String model;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public SocialPostGenerationService(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    public CompanyAnalysisAndPostDto generatePostAndAnalysis(ScrapedContent content, String toneOfVoice) {
        log.info("Avvio analisi ed elaborazione post con Gemini per URL: {} (Tono: {})", content.getUrl(), toneOfVoice);

        String prompt = """
                Sei un Senior Marketing Director, esperto in Business Intelligence e Social Media Copywriting per Facebook.
                
                Compito:
                1. Analizza il testo estratto dal sito web (%s) ed estrai con accuratezza:
                   - Nome azienda
                   - Oggetto sociale e attività principale svolta
                   - Target ideale (es. B2B, PMI, Consumer)
                   - Unique Selling Proposition (USP) / punti di forza
                2. Scrivi un post Facebook coinvolgente e professionale in lingua ITALIANA:
                   - Gancio iniziale (hook) forte con emoji
                   - Corpo del post formattato bene con ritmo ed emoji contestuali
                   - Call to Action (CTA) finale efficace
                   - 3-5 hashtag mirati
                3. Genera un prompt visivo descrittivo in lingua INGLESE per generare un'immagine coordinata di altissima qualità fotografica o grafica 3D moderna (senza testo inserito dentro l'immagine).
                
                Tono di voce richiesto: %s.
                
                === TESTO ESTRATTO DAL SITO ===
                %s
                
                IMPORTANTE: Rispondi ESCLUSIVAMENTE con un JSON valido con questa esatta struttura, senza blocchi di codice markdown attorno o testo prima/dopo:
                {
                  "companyName": "Nome Azienda",
                  "businessPurpose": "Descrizione chiara e completa dell'oggetto sociale",
                  "targetAudience": "Target di clientela",
                  "uniqueSellingProposition": "Punto di forza principale",
                  "hook": "Gancio iniziale con emoji",
                  "postBody": "Corpo del post su più righe",
                  "callToAction": "Invito all'azione finale",
                  "hashtags": ["#Tag1", "#Tag2", "#Tag3"],
                  "imagePrompt": "Detailed visual prompt in English..."
                }
                """.formatted(content.getUrl(), toneOfVoice, content.getMainText());

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> contents = Map.of("parts", List.of(part));
        
        // Configurazione per garantire output JSON
        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "temperature", 0.7
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(contents),
                "generationConfig", generationConfig
        );

        String url = String.format("%s/models/%s:generateContent?key=%s", baseUrl, model, apiKey.trim());

        try {
            String jsonResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidate = root.path("candidates").get(0);
            String textContent = candidate.path("content").path("parts").get(0).path("text").asText();

            log.info("Risposta ricevuta da Gemini. Parsing JSON in corso...");
            return parseGeminiJson(textContent);

        } catch (Exception e) {
            log.error("Errore durante la chiamata alle API di Google Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Errore nell'elaborazione con Google Gemini: " + e.getMessage(), e);
        }
    }

    private CompanyAnalysisAndPostDto parseGeminiJson(String rawJson) {
        try {
            // Pulisce eventuali delimitatori markdown se presenti (```json ... ```)
            String cleaned = rawJson.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            return objectMapper.readValue(cleaned, CompanyAnalysisAndPostDto.class);
        } catch (Exception e) {
            log.warn("Parsing diretto fallito, fallback manuale: {}", e.getMessage());
            CompanyAnalysisAndPostDto dto = new CompanyAnalysisAndPostDto();
            dto.setCompanyName("Azienda");
            dto.setBusinessPurpose("Oggetto sociale estratto dal sito");
            dto.setPostBody(rawJson);
            dto.setHashtags(List.of("#business", "#innovazione"));
            return dto;
        }
    }
}
