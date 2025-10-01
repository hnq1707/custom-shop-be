package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.service.DesignService;
import org.hnq.ecommerce_be.service.TranslationService;
import org.hnq.ecommerce_be.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.logging.Logger;

@Service
public class SimpleDesignService implements DesignService {

    private static final Logger log = Logger.getLogger(SimpleDesignService.class.getName());

    private final TranslationService translationService;
    private final FileUtil fileUtil;

    @Value("${app.ai.provider:hf}")
    private String provider;

    @Value("${app.ai.hf.api-key:}")
    private String hfApiKey;

    @Value("${app.ai.hf.text2img-model:stabilityai/stable-diffusion-xl-base-1.0}")
    private String hfText2ImgModel;

    public SimpleDesignService(TranslationService translationService, FileUtil fileUtil) {
        this.translationService = translationService;
        this.fileUtil = fileUtil;
    }

    @Override
    public String generateDesign(String vietnameseDescription, MultipartFile imageSample) {
        String translatedPrompt = translationService.translateToEnglish(vietnameseDescription);
        String prompt = buildPrompt(translatedPrompt);

        try {
            log.info("🧠 Generating design with Hugging Face model: " + hfText2ImgModel);
            byte[] png = generateWithHuggingFace(prompt, imageSample);
            return fileUtil.saveDesignPng(png);
        } catch (Exception e) {
            log.severe("❌ Error during generation: " + e.getMessage());
            throw new RuntimeException("Failed to generate design via Hugging Face: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateDesign(String vietnameseDescription) {
        return generateDesign(vietnameseDescription, null);
    }

    private String buildPrompt(String userPromptEn) {
        String up = userPromptEn == null ? "" : userPromptEn.trim();
        String template = """
                Ultra-detailed vector illustration for T-shirt print, transparent PNG with alpha, centered composition, \
                clean edges, high contrast, no background, no watermark. Subject: %s. \
                Modern, minimalist, suitable for printing on white shirt.
                """;
        return String.format(template, up);
    }

    private byte[] generateWithHuggingFace(String prompt, MultipartFile sample) throws IOException, InterruptedException {
        if (hfApiKey == null || hfApiKey.isBlank()) {
            throw new IllegalStateException("Hugging Face API key not configured.");
        }

        String url = "https://api-inference.huggingface.co/models/" + hfText2ImgModel;
        log.info("🔗 Requesting Hugging Face model URL: " + url);

        String json;
        if (sample != null && !sample.isEmpty()) {
            // image-to-image mode
            byte[] sampleBytes = sample.getBytes();
            String base64 = Base64.getEncoder().encodeToString(sampleBytes);
            String dataUri = "data:image/png;base64," + base64;

            json = """
            {
              "inputs": {
                "image": %s,
                "prompt": %s
              },
              "options": { "wait_for_model": true }
            }
            """.formatted(toJsonString(dataUri), toJsonString(prompt));

        } else {
            // text-to-image mode
            json = """
            {
              "inputs": %s,
              "options": { "wait_for_model": true }
            }
            """.formatted(toJsonString(prompt));
        }

        log.info("📤 Sending request to Hugging Face with prompt: " + prompt);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(180))
                .header("Authorization", "Bearer " + hfApiKey)
                .header("Accept", "image/png")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        int status = response.statusCode();
        String contentType = response.headers().firstValue("content-type").orElse("");
        log.info("📥 Received response: status=" + status + ", contentType=" + contentType);

        if (status >= 200 && status < 300 && contentType.contains("image")) {
            log.info("✅ Hugging Face generation success.");
            return response.body();
        }

        // If Hugging Face returns error
        String errMsg = new String(response.body(), StandardCharsets.UTF_8);
        log.severe("🚨 Hugging Face API error response: " + errMsg);

        throw new IOException("""
                Hugging Face API failed:
                - URL: %s
                - Model: %s
                - Status: %d
                - Content-Type: %s
                - Body: %s
                """.formatted(url, hfText2ImgModel, status, contentType, errMsg));
    }

    private static String toJsonString(String s) {
        String str = s == null ? "" : s;
        String escaped = str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }
}
