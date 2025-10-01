package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.service.TranslationService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class SimpleTranslationService implements TranslationService {

    private static final String GOOGLE_TRANSLATE_URL =
            "https://translate.googleapis.com/translate_a/single?client=gtx&sl=vi&tl=en&dt=t&q=";

    @Override
    public String translateToEnglish(String vietnameseText) {
        if (vietnameseText == null || vietnameseText.isBlank()) {
            return "";
        }

        try {
            String encoded = URLEncoder.encode(vietnameseText, StandardCharsets.UTF_8);
            String url = GOOGLE_TRANSLATE_URL + encoded;

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractTranslatedText(response.body());
            } else {
                System.err.println("⚠️ Google Translate error: " + response.statusCode() + " - " + response.body());
                return vietnameseText; // fallback
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("⚠️ Translation error: " + e.getMessage());
            return vietnameseText;
        }
    }

    private static String extractTranslatedText(String body) {
        // Response format: [[[ "Hello world", "Xin chào thế giới", null, null,... ]]]
        int start = body.indexOf("[[[\"");
        if (start == -1) return body;
        start += 4;
        int end = body.indexOf("\"", start);
        if (end == -1) return body;
        return body.substring(start, end);
    }
}
