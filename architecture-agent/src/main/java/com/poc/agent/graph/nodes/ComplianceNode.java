package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;
import com.poc.agent.mcp.McpClient;

import java.util.Map;

@Component
public class ComplianceNode implements Node<ArchState> {

    private final McpClient mcp;

    public ComplianceNode(McpClient mcp) { this.mcp = mcp; }

    @Override
    public ArchState apply(ArchState state) {
        try {
            Object archObj = state.getExtractedComponentsJson() != null ? state.getExtractedComponentsJson()
                    : Map.of("business", state.getBusinessReq(), "nfr", state.getNfrReq());
            Map compli = mcp.checkCompliance(Map.of("architecture", archObj));
            state.setComplianceJson(compli!=null?compli.toString():null);
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
