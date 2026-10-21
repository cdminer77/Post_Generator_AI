package com.example.postgenerator.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGenerationRequest {

    @NotBlank(message = "L'URL del sito aziendale è obbligatorio")
    @URL(message = "Inserisci un URL valido (es. https://www.esempio.it)")
    private String websiteUrl;

    @Builder.Default
    private String toneOfVoice = "Professionale & Coinvolgente"; // Opzioni: Istituzionale, Storytelling, Promozionale, ecc.

    @Builder.Default
    private String imageOrientation = "SQUARE"; // SQUARE (1080x1080) o LANDSCAPE (1200x630 / wide)

    @Builder.Default
    private boolean generateImage = true;
}
