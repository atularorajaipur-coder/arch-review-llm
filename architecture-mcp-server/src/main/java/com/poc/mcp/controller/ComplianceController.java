package com.poc.mcp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tool/compliance")
public class ComplianceController {

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String OPENAI_KEY = System.getenv("OPENAI_API_KEY");
    private final String CHAT_MODEL = System.getenv().getOrDefault("OPENAI_CHAT_MODEL", "gpt-4o");

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody Map<String,Object> body,
                                   @RequestParam(value="country", required=false, defaultValue="IN") String country) throws Exception {

        String bodyJson = mapper.writeValueAsString(body);

        String prompt = String.format(
            "You are a senior compliance auditor for banking systems. Evaluate the architecture JSON below for PCI-DSS, GDPR, RBI and return STRICT JSON in the format: {\n  \"risk_score\": number,\n  \"issues\": [{\"issue\":\"\", \"severity\":\"low/medium/high\", \"component\":\"\"}],\n  \"recommendations\": [\"...\"]\n}\nArchitecture: %s\nCountry context: %s",
            bodyJson, country
        );

        Map<String,Object> req = Map.of(
                "model", CHAT_MODEL,
                "messages", List.of(Map.of("role","user","content",prompt)),
                "max_tokens", 700
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
