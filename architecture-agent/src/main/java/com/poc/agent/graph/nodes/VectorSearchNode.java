package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;
import com.poc.agent.embedding.OpenAiEmbeddingClient;
import com.poc.agent.mcp.McpClient;

import java.util.Map;

@Component
public class VectorSearchNode implements Node<ArchState> {

    private final OpenAiEmbeddingClient emb;
    private final McpClient mcp;

    public VectorSearchNode(OpenAiEmbeddingClient emb, McpClient mcp) { this.emb = emb; this.mcp = mcp; }

    @Override
    public ArchState apply(ArchState state) {
        try {
            String text = state.getExtractedComponentsJson() != null ? state.getExtractedComponentsJson()
                    : (state.getBusinessReq()==null?"":state.getBusinessReq()) + " " + (state.getNfrReq()==null?"":state.getNfrReq());
            var vec = emb.embed(text);
            Map resp = mcp.queryPatterns(Map.of("embedding", vec, "k", 3));
            state.setSimilarPatternsJson(resp!=null?resp.toString():null);
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
