package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class AnalyzeNode implements Node<ArchState> {

    private final String OPENAI_KEY = System.getenv("OPENAI_API_KEY");
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ArchState apply(ArchState state) {
        try {
            String prompt = String.format("""You are a senior software architect.\nBusiness requirements: %s\nNFRs: %s\nExtracted components (if any): %s\nProvide a concise analysis (gaps, bottlenecks, compliance flags). Output plain text.""", 
                state.getBusinessReq()==null?"":state.getBusinessReq(),
                state.getNfrReq()==null?"":state.getNfrReq(),
                state.getExtractedComponentsJson()==null?"":state.getExtractedComponentsJson());

            Map<String,Object> req = Map.of("model","gpt-4o","messages",java.util.List.of(Map.of("role","user","content",prompt)),"max_tokens",400);
            OkHttpClient client = new OkHttpClient();
            RequestBody rb = RequestBody.create(mapper.writeValueAsString(req), MediaType.get("application/json"));
            Request r = new Request.Builder().url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization","Bearer "+OPENAI_KEY).post(rb).build();

            try (Response resp = client.newCall(r).execute()) {
                Map respJson = mapper.readValue(resp.body().string(), Map.class);
                Map choice = (Map)((java.util.List)respJson.get("choices")).get(0);
                Map message = (Map)choice.get("message");
                String content = (String)message.get("content");
                state.setAnalysisText(content);
                return state;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
