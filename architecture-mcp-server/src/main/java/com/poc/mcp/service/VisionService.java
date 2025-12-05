package com.poc.mcp.service;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VisionService {

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String openAiKey = System.getenv("OPENAI_API_KEY");
    private final String visionModel = System.getenv().getOrDefault("OPENAI_VISION_MODEL", "gpt-4o-mini-vision");

    public String extractFromImageBase64(String base64Image, String prompt) throws Exception {
        Map<String,Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        Map<String,Object> content = Map.of(
                "type", "input_image_with_text",
                "text", prompt,
                "image_base64", base64Image
        );
        userMessage.put("content", content);

        Map<String,Object> req = new LinkedHashMap<>();
        req.put("model", visionModel);
        req.put("messages", List.of(userMessage));
        req.put("max_tokens", 800);

        RequestBody rb = RequestBody.create(mapper.writeValueAsString(req), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + openAiKey)
                .post(rb)
                .build();

        try (Response resp = http.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                String body = resp.body() != null ? resp.body().string() : "";
                throw new RuntimeException("OpenAI Vision call failed: " + resp.code() + " - " + body);
            }
            String respBody = resp.body().string();
            Map<?,?> json = mapper.readValue(respBody, Map.class);
            List<?> choices = (List<?>) json.get("choices");
            if (choices == null || choices.isEmpty()) return respBody;
            Map<?,?> first = (Map<?,?>) choices.get(0);
            Map<?,?> message = (Map<?,?>) first.get("message");
            Object contentObj = message.get("content");
            if (contentObj instanceof String) return (String) contentObj;
            return mapper.writeValueAsString(contentObj);
        }
    }
}
