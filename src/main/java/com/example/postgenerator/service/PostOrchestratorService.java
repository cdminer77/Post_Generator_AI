package com.example.postgenerator.service;

import com.example.postgenerator.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostOrchestratorService {

    private final WebScraperService webScraperService;
    private final SocialPostGenerationService socialPostGenerationService;
    private final ImageGenerationService imageGenerationService;

    public PostGenerationResponse processWebsiteAndGeneratePost(PostGenerationRequest request) {
        log.info("Ricevuta richiesta generazione post per URL: {}", request.getWebsiteUrl());

        try {
            // 1. Scraping del sito web
            ScrapedContent content = webScraperService.extractCompanyInfo(request.getWebsiteUrl());

            // 2. Analisi aziendale e creazione del Post con Spring AI
            CompanyAnalysisAndPostDto aiResult = socialPostGenerationService.generatePostAndAnalysis(
                    content,
                    request.getToneOfVoice()
            );

            // 3. Generazione immagine (opzionale o default attiva)
            String imageUrl = null;
            if (request.isGenerateImage() && aiResult.getImagePrompt() != null && !aiResult.getImagePrompt().isBlank()) {
                imageUrl = imageGenerationService.generateFacebookImage(
                        aiResult.getImagePrompt(),
                        request.getImageOrientation()
                );
            }

            // 4. Composizione del testo formattato completo
            String fullFormattedPost = buildFormattedPost(aiResult);

            return PostGenerationResponse.builder()
                    .success(true)
                    .companyName(aiResult.getCompanyName())
                    .businessPurpose(aiResult.getBusinessPurpose())
                    .targetAudience(aiResult.getTargetAudience())
                    .uniqueSellingProposition(aiResult.getUniqueSellingProposition())
                    .hook(aiResult.getHook())
                    .postBody(aiResult.getPostBody())
                    .callToAction(aiResult.getCallToAction())
                    .hashtags(aiResult.getHashtags())
                    .fullFormattedPost(fullFormattedPost)
                    .imagePrompt(aiResult.getImagePrompt())
                    .imageUrl(imageUrl)
                    .imageDimensions("LANDSCAPE".equalsIgnoreCase(request.getImageOrientation()) ? "1792 x 1024 (Wide)" : "1024 x 1024 (Quadrato)")
                    .build();

        } catch (Exception e) {
            log.error("Errore durante l'elaborazione del post per {}: {}", request.getWebsiteUrl(), e.getMessage(), e);
            return PostGenerationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private String buildFormattedPost(CompanyAnalysisAndPostDto dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getHook() != null) {
            sb.append(dto.getHook()).append("\n\n");
        }
        if (dto.getPostBody() != null) {
            sb.append(dto.getPostBody()).append("\n\n");
        }
        if (dto.getCallToAction() != null) {
            sb.append(dto.getCallToAction()).append("\n\n");
        }
        if (dto.getHashtags() != null && !dto.getHashtags().isEmpty()) {
            sb.append(String.join(" ", dto.getHashtags()));
        }
        return sb.toString().trim();
    }
}
