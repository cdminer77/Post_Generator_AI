package com.example.postgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGenerationResponse {
    private boolean success;
    private String errorMessage;

    // Analisi azienda
    private String companyName;
    private String businessPurpose; // Oggetto sociale sintetizzato
    private String targetAudience;
    private String uniqueSellingProposition;

    // Post Facebook generato
    private String hook;
    private String postBody;
    private String callToAction;
    private List<String> hashtags;
    private String fullFormattedPost;

    // Immagine Facebook
    private String imagePrompt;
    private String imageUrl;
    private String imageDimensions;
}
