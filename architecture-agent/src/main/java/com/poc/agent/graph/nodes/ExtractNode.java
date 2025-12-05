package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.mcp.McpClient;
import com.poc.agent.graph.ArchState;

import java.util.Map;

@Component
public class ExtractNode implements Node<ArchState> {

    private final McpClient mcp;

    public ExtractNode(McpClient mcp) { this.mcp = mcp; }

    @Override
    public ArchState apply(ArchState state) {
        try {
            if (state.getArchImage() != null && state.getArchImage().length > 0) {
                Map parsed = mcp.parseArchMultipart(state.getArchImage(), null);
                state.setExtractedComponentsJson(parsed != null ? parsed.toString() : null);
            } else {
                state.setExtractedComponentsJson(null);
            }
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
