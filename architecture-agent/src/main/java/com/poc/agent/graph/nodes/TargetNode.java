package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;
import com.poc.agent.mcp.McpClient;

import java.util.Map;

@Component
public class TargetNode implements Node<ArchState> {

    private final McpClient mcp;

    public TargetNode(McpClient mcp) { this.mcp = mcp; }

    @Override
    public ArchState apply(ArchState state) {
        try {
            Map payload = Map.of(
                    "architecture", state.getExtractedComponentsJson() != null ? state.getExtractedComponentsJson() : Map.of("business", state.getBusinessReq(), "nfr", state.getNfrReq()),
                    "compliance", state.getComplianceJson() != null ? state.getComplianceJson() : Map.of(),
                    "patternsContext", state.getSimilarPatternsJson() != null ? state.getSimilarPatternsJson() : Map.of()
            );
            Map improved = mcp.improve(payload);
            state.setTargetArchitectureJson(improved != null ? improved.toString() : null);
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
