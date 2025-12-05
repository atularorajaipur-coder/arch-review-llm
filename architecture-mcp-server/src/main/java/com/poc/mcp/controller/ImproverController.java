package com.poc.mcp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tool/improver")
public class ImproverController {

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String OPENAI_KEY = System.getenv("OPENAI_API_KEY");
    private final String CHAT_MODEL = System.getenv().getOrDefault("OPENAI_CHAT_MODEL", "gpt-4o");

    @PostMapping("/improve")
    public ResponseEntity<?> improve(@RequestBody Map<String,Object> body) throws Exception {
        String architectureJson = mapper.writeValueAsString(body.getOrDefault("architecture", Map.of()));
        String complianceJson = mapper.writeValueAsString(body.getOrDefault("compliance", Map.of()));
        String patternsJson = mapper.writeValueAsString(body.getOrDefault("patternsContext", List.of()));

        String prompt = String.format(
            "You are a Senior Principal Architect for banking systems. Using the reference patterns below and the current architecture + compliance information, produce a target architecture JSON and a mermaid diagram. Return STRICT JSON in this format: {\n  \"target_architecture\": { \"services\": [...], \"databases\":[...], \"network\":[...], \"security\":[...] },\n  \"mermaid\": \"flowchart ...\",\n  \"summary\": \"brief summary\",\n  \"key_changes\": [...],\n  \"modernization_score\": 0-100\n}\nPatterns: %s\nCurrent architecture: %s\nCompliance: %s",
            patternsJson, architectureJson, complianceJson
        );

        Map<String,Object> req = Map.of(
                "model", CHAT_MODEL,
                "messages", List.of(Map.of("role","user","content",prompt)),
                "max_tokens", 1500
        );

        RequestBody rb = RequestBody.create(mapper.writeValueAsString(req), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + OPENAI_KEY)
                .post(rb)
                .build();

        try (Response resp = http.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                String bodyStr = resp.body() != null ? resp.body().string() : "";
                throw new RuntimeException("OpenAI call failed: " + resp.code() + " - " + bodyStr);
            }
            String respBody = resp.body().string();
            Map<String,Object> json = mapper.readValue(respBody, Map.class);
            List choices = (List) json.get("choices");
            Map first = (Map) choices.get(0);
            Map message = (Map) first.get("message");
            String content = (String) message.get("content");
            Map parsed = mapper.readValue(content, Map.class);
            return ResponseEntity.ok(parsed);
        }
    }
}
