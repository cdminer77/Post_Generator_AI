package com.example.postgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ImageGenerationService {

    public String generateFacebookImage(String promptText, String orientation) {
        log.info("Generazione immagine per Facebook con orientamento: {}", orientation);

        int width = 1080;
        int height = 1080;

        if ("LANDSCAPE".equalsIgnoreCase(orientation)) {
            width = 1200;
            height = 630;
        }

        try {
            // Arricchisce il prompt per garantire qualità fotografica e assenza di scritte distorte
            String enhancedPrompt = promptText + ", 8k resolution, cinematic lighting, corporate commercial photography, professional framing, no watermark, no text";
            String encodedPrompt = URLEncoder.encode(enhancedPrompt, StandardCharsets.UTF_8);

            // Endpoint di generazione visiva ad alta risoluzione senza watermark
            String imageUrl = String.format("https://image.pollinations.ai/prompt/%s?width=%d&height=%d&nologo=true&seed=%d",
                    encodedPrompt, width, height, (int)(Math.random() * 100000));

            log.info("Immagine generata con successo per le dimensioni {}x{}: {}", width, height, imageUrl);
            return imageUrl;

        } catch (Exception e) {
            log.error("Errore durante la creazione dell'immagine: {}", e.getMessage(), e);
            return null;
        }
    }
}
