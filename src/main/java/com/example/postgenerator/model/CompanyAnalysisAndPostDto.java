package com.example.postgenerator.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class CompanyAnalysisAndPostDto {

    @JsonPropertyDescription("Nome dell'azienda individuato nel sito")
    private String companyName;

    @JsonPropertyDescription("Sintesi chiara ed esaustiva dell'oggetto sociale e del core business dell'azienda")
    private String businessPurpose;

    @JsonPropertyDescription("Target di clientela ideale dell'azienda (es. B2B, PMI, Privati, Sanità)")
    private String targetAudience;

    @JsonPropertyDescription("La Unique Selling Proposition o punto di forza differenziante")
    private String uniqueSellingProposition;

    @JsonPropertyDescription("Frase gancio (hook) iniziale per catturare l'attenzione su Facebook")
    private String hook;

    @JsonPropertyDescription("Corpo del post per Facebook, formattato con emoji e spaziatura ottimale")
    private String postBody;

    @JsonPropertyDescription("Call to action conclusiva che invita al contatto, alla visita o all'interazione")
    private String callToAction;

    @JsonPropertyDescription("Lista di 3-5 hashtag pertinenti e popolari")
    private List<String> hashtags;

    @JsonPropertyDescription("Prompt dettagliato in lingua INGLESE per generare un'immagine coordinata con DALL-E 3 (fotografica o grafica 3D pulita, nessun testo illeggibile)")
    private String imagePrompt;
}
