package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;
import com.poc.agent.mcp.McpClient;

import java.util.Map;

@Component
public class MermaidNode implements Node<ArchState> {

    private final McpClient mcp;

    public MermaidNode(McpClient mcp) { this.mcp = mcp; }

    @Override
    public ArchState apply(ArchState state) {
        try {
            Object arch = state.getTargetArchitectureJson() != null ? state.getTargetArchitectureJson() : Map.of();
            Map mer = mcp.generateMermaid(arch);
            state.setMermaid(mer!=null?mer.toString():null);
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
