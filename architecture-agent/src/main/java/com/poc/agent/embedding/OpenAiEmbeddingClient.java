package com.poc.agent.embedding;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OpenAiEmbeddingClient {
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey = System.getenv("OPENAI_API_KEY");
    private final String model = System.getenv().getOrDefault("OPENAI_EMBEDDING_MODEL","text-embedding-3-small");

    public List<Double> embed(String text) throws Exception {
        Map<String,Object> payload = Map.of("model", model, "input", text);
        RequestBody rb = RequestBody.create(mapper.writeValueAsString(payload), MediaType.get("application/json"));
        Request req = new Request.Builder()
                .url("https://api.openai.com/v1/embeddings")
                .addHeader("Authorization","Bearer "+apiKey)
                .post(rb)
                .build();
        try (Response r = http.newCall(req).execute()) {
            if (!r.isSuccessful()) throw new RuntimeException("OpenAI embeddings failed: "+r.code());
            Map json = mapper.readValue(r.body().string(), Map.class);
            Map data0 = (Map)((List)json.get("data")).get(0);
            return (List<Double>) data0.get("embedding");
        }
    }
}
