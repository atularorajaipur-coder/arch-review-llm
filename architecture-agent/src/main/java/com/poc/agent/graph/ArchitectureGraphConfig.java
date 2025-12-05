package com.poc.agent.graph;

import org.langchain4j.graph.Graph;
import org.langchain4j.graph.GraphBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArchitectureGraphConfig {

    @Bean
    public Graph<ArchState> archGraph(
            com.poc.agent.graph.nodes.ExtractNode extractNode,
            com.poc.agent.graph.nodes.AnalyzeNode analyzeNode,
            com.poc.agent.graph.nodes.VectorSearchNode vectorNode,
            com.poc.agent.graph.nodes.ComplianceNode complianceNode,
            com.poc.agent.graph.nodes.TargetNode targetNode,
            com.poc.agent.graph.nodes.MermaidNode mermaidNode,
            com.poc.agent.graph.nodes.FinalNode finalNode
    ) {
        return GraphBuilder.<ArchState>create()
                .addNode("extract", extractNode)
                .addNode("analyze", analyzeNode)
                .addNode("vector", vectorNode)
                .addNode("compliance", complianceNode)
                .addNode("target", targetNode)
                .addNode("mermaid", mermaidNode)
                .addNode("final", finalNode)
                .addEdge("extract", "analyze")
                .addEdge("analyze", "vector")
                .addEdge("vector", "compliance")
                .addEdge("compliance", "target")
                .addEdge("target", "mermaid")
                .addEdge("mermaid", "final")
                .build();
    }
}
