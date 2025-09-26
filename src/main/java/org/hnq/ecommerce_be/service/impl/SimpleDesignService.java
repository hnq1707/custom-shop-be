package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.service.DesignService;
import org.hnq.ecommerce_be.service.TranslationService;
import org.hnq.ecommerce_be.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class SimpleDesignService implements DesignService {

    private final TranslationService translationService;
    private final FileUtil fileUtil;

    @Value("${app.ai.provider:stub}")
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
        String promptEn = buildPrompt(translationService.translateToEnglish(vietnameseDescription));
        try {
            byte[] png = generateWithProvider(promptEn, imageSample);
            return fileUtil.saveDesignPng(png);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate design", e);
        }
    }

    @Override
    public String generateDesign(String vietnameseDescription) {
        String promptEn = buildPrompt(translationService.translateToEnglish(vietnameseDescription));
        try {
            byte[] png = generateWithProvider(promptEn, null);
            return fileUtil.saveDesignPng(png);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate design", e);
        }
    }

    // Compose a template prompt for better T-shirt print results
    private String buildPrompt(String userPromptEn) {
        String up = userPromptEn == null ? "" : userPromptEn.trim();
        String template = "Ultra-detailed vector illustration for T-shirt print, transparent PNG with alpha, centered composition, clean edges, high contrast, no background, no text, no watermark. Subject: %s. Minimalist, modern, suitable for printing on white shirt.";
        return String.format(template, up);
    }

    private byte[] generateWithProvider(String prompt, MultipartFile sample) throws IOException {
        // Prefer HuggingFace if configured, fallback to local stub image
        if ("hf".equalsIgnoreCase(provider) && hfApiKey != null && !hfApiKey.isBlank()) {
            try {
                return hfTextToImage(prompt);
            } catch (Exception ex) {
                // Fallback to stub renderer if HF fails
            }
        }
        return createTransparentPng(prompt, sample);
    }

    private byte[] hfTextToImage(String prompt) throws IOException, InterruptedException {
        String url = "https://api-inference.huggingface.co/models/" + hfText2ImgModel;
        String json = "{\"inputs\":" + toJsonString(prompt) + ",\"options\":{\"wait_for_model\":true}}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .header("Authorization", "Bearer " + hfApiKey)
                .header("Accept", "image/png")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        String contentType = response.headers().firstValue("content-type").orElse("");

        if (response.statusCode() >= 200 && response.statusCode() < 300 && contentType.contains("image")) {
            return response.body();
        }
        // If response is JSON error, throw exception to trigger fallback
        throw new IOException("HuggingFace generation failed: status=" + response.statusCode() + ", contentType=" + contentType);
    }

    private static String toJsonString(String s) {
        String str = s == null ? "" : s;
        String escaped = str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }

    private byte[] createTransparentPng(String prompt, MultipartFile sample) throws IOException {
        int size = 1024;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Transparent background (already by TYPE_INT_ARGB)

        // If sample image provided, draw it centered and scaled
        if (sample != null && !sample.isEmpty()) {
            try {
                BufferedImage sampleImg = ImageIO.read(sample.getInputStream());
                if (sampleImg != null) {
                    int targetW = (int) (size * 0.8);
                    int targetH = (int) (size * 0.8);
                    int x = (size - targetW) / 2;
                    int y = (size - targetH) / 2;
                    g2d.drawImage(sampleImg.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH), x, y, null);
                }
            } catch (Exception ignored) { }
        }

        // Draw prompt text overlay
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
        String title = "AI DESIGN";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (size - titleWidth) / 2, 80);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 24));
        String text = prompt == null ? "" : prompt;
        // split text into lines roughly 40 chars
        int y = 130;
        int maxChars = 40;
        while (text.length() > 0 && y < size - 40) {
            int end = Math.min(maxChars, text.length());
            String line = text.substring(0, end);
            int w = g2d.getFontMetrics().stringWidth(line);
            g2d.drawString(line, (size - w) / 2, y);
            text = text.substring(end);
            y += 32;
        }
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
