package com.example.postgenerator.service;

import com.example.postgenerator.model.ScrapedContent;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class WebScraperService {

    @Value("${app.scraper.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${app.scraper.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36}")
    private String userAgent;

    @Value("${app.scraper.max-extracted-chars:12000}")
    private int maxExtractedChars;

    private static final String[] RELEVANT_KEYWORDS = {
            "chi siamo", "chi-siamo", "about", "azienda", "servizi", "mission", "storia", "cosa facciamo"
    };

    public ScrapedContent extractCompanyInfo(String targetUrl) {
        log.info("Iniziando scraping per target URL: {}", targetUrl);
        StringBuilder textCollector = new StringBuilder();
        List<String> visitedPages = new ArrayList<>();
        Set<String> visitedUrls = new HashSet<>();

        String normalizedUrl = normalizeUrl(targetUrl);
        visitedUrls.add(normalizedUrl);
        visitedPages.add(normalizedUrl);

        String pageTitle = "";
        String metaDescription = "";

        Document homeDoc = fetchDocumentSafely(normalizedUrl);
        if (homeDoc != null) {
            pageTitle = homeDoc.title();
            Element metaDescEl = homeDoc.selectFirst("meta[name=description], meta[property=og:description]");
            if (metaDescEl != null) {
                metaDescription = metaDescEl.attr("content");
            }

            String homeText = extractCleanText(homeDoc);
            textCollector.append("=== HOME PAGE ===\n");
            if (!pageTitle.isBlank()) {
                textCollector.append("Titolo: ").append(pageTitle).append("\n");
            }
            if (!metaDescription.isBlank()) {
                textCollector.append("Descrizione Meta: ").append(metaDescription).append("\n");
            }
            if (!homeText.isBlank()) {
                textCollector.append("Contenuto Principale:\n").append(homeText).append("\n\n");
            }

            // Ricerca e visita di pagine secondarie rilevanti
            List<String> subPageUrls = findRelevantSubPages(homeDoc, normalizedUrl);
            for (String subUrl : subPageUrls) {
                if (visitedUrls.add(subUrl) && visitedUrls.size() <= 3) {
                    Document subDoc = fetchDocumentSafely(subUrl);
                    if (subDoc != null) {
                        visitedPages.add(subUrl);
                        textCollector.append("=== SEZIONE: ").append(subDoc.title()).append(" (").append(subUrl).append(") ===\n");
                        textCollector.append(extractCleanText(subDoc)).append("\n\n");
                        if (textCollector.length() >= maxExtractedChars) {
                            break;
                        }
                    }
                }
            }
        }

        // Se il sito ha protezioni anti-scraping o testo insufficiente, aggiunge contesto di fallback per Gemini
        if (textCollector.length() < 150) {
            String domain = extractHost(normalizedUrl);
            log.info("Testo estratto limitato per {}. Aggiunta fallback contestuale per brand intelligence.", normalizedUrl);
            textCollector.append("=== DETTAGLI DOMINIO AZIENDALE ===\n");
            textCollector.append("URL: ").append(normalizedUrl).append("\n");
            textCollector.append("Brand / Dominio: ").append(domain).append("\n");
            if (!pageTitle.isBlank()) {
                textCollector.append("Titolo rilevato: ").append(pageTitle).append("\n");
            }
            if (!metaDescription.isBlank()) {
                textCollector.append("Meta: ").append(metaDescription).append("\n");
            }
            textCollector.append("Istruzione per l'AI: Utilizza la tua conoscenza approfondita e aggiornata su questa azienda/brand ('")
                    .append(domain).append("') per determinare con precisione l'oggetto sociale, il target e generare il post.\n");
        }

        String finalContent = textCollector.toString();
        if (finalContent.length() > maxExtractedChars) {
            finalContent = finalContent.substring(0, maxExtractedChars) + "\n... [Contenuto troncato per limite di contesto]";
        }

        return ScrapedContent.builder()
                .url(targetUrl)
                .pageTitle(pageTitle)
                .metaDescription(metaDescription)
                .mainText(finalContent)
                .secondaryPagesVisited(visitedPages)
                .build();
    }

    private Document fetchDocumentSafely(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(connectTimeoutMs)
                    .followRedirects(true)
                    .ignoreHttpErrors(true) // Evita eccezioni 403/404 da server terzi
                    .get();
        } catch (Exception e) {
            log.warn("Jsoup fallito per {}: {}. Tentativo con Java HttpClient nativo...", url, e.getMessage());
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", userAgent)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return Jsoup.parse(response.body(), url);
            } catch (Exception ex) {
                log.warn("Anche fallback HttpClient fallito per {}: {}", url, ex.getMessage());
                return null;
            }
        }
    }

    private String extractCleanText(Document doc) {
        Document cleanDoc = doc.clone();
        cleanDoc.select("script, style, noscript, svg, nav, footer, form, iframe, [class*='cookie'], [id*='cookie'], [class*='banner'], [class*='popup']").remove();

        StringBuilder sb = new StringBuilder();
        Elements headingsAndParagraphs = cleanDoc.select("h1, h2, h3, h4, p, li");
        for (Element el : headingsAndParagraphs) {
            String text = el.text().trim();
            if (text.length() > 20) {
                sb.append(text).append("\n");
            }
        }
        return sb.toString();
    }

    private List<String> findRelevantSubPages(Document doc, String baseUrl) {
        List<String> relevantLinks = new ArrayList<>();
        Elements links = doc.select("a[href]");
        String baseHost = extractHost(baseUrl);

        for (Element link : links) {
            String href = link.absUrl("href");
            String anchorText = link.text().toLowerCase();

            if (href.isBlank() || href.startsWith("#") || href.startsWith("javascript:")) {
                continue;
            }

            String linkHost = extractHost(href);
            if (!baseHost.equalsIgnoreCase(linkHost)) {
                continue;
            }

            for (String kw : RELEVANT_KEYWORDS) {
                if (anchorText.contains(kw) || href.toLowerCase().contains(kw.replace(" ", "-"))) {
                    relevantLinks.add(href);
                    break;
                }
            }

            if (relevantLinks.size() >= 2) {
                break;
            }
        }
        return relevantLinks;
    }

    private String normalizeUrl(String url) {
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://" + trimmed;
        }
        return trimmed;
    }

    private String extractHost(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return (host != null) ? host.replaceFirst("^www\\.", "") : "";
        } catch (Exception e) {
            return "";
        }
    }
}
