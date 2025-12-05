package com.poc.agent.mcp;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class McpClient {
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String base;

    public McpClient() {
        this.base = System.getenv().getOrDefault("MCP_BASE","http://localhost:8080/tool");
    }

    private Request buildPost(String url, Object body) throws Exception {
        RequestBody rb = RequestBody.create(mapper.writeValueAsBytes(body), MediaType.get("application/json"));
        return new Request.Builder().url(url).post(rb).build();
    }

    public Map parseArchMultipart(byte[] bytes, String hint) throws Exception {
        RequestBody rb = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "diagram.png", RequestBody.create(bytes, MediaType.parse("image/png")))
                .addFormDataPart("hint", hint==null?"":hint)
                .build();
        Request req = new Request.Builder().url(base + "/arch/parse").post(rb).build();
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }

    public Map checkCompliance(Map payload) throws Exception {
        Request req = buildPost(base + "/compliance/check", payload);
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }

    public Map improve(Map payload) throws Exception {
        Request req = buildPost(base + "/improver/improve", payload);
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }

    public Map generateMermaid(Object payload) throws Exception {
        Request req = buildPost(base + "/mermaid/generate", payload);
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }

    public Map ingestPattern(Object payload) throws Exception {
        Request req = buildPost(base + "/patterns/ingest", payload);
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }

    public Map queryPatterns(Object payload) throws Exception {
        Request req = buildPost(base + "/patterns/query", payload);
        try (Response r = http.newCall(req).execute()) {
            String body = r.body().string();
            return mapper.readValue(body, Map.class);
        }
    }
}
