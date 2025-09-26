package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.service.TranslationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SimpleTranslationService implements TranslationService {

    @Value("${app.ai.provider:stub}")
    private String provider;

    @Value("${app.ai.openai.api-key:}")
    private String openAiKey;

    @Override
    public String translateToEnglish(String vietnameseText) {
        // Minimal stub: return the original text. In real usage, call OpenAI/Google here.
        if (vietnameseText == null) return "";
        return vietnameseText;
    }
}
