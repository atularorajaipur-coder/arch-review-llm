package com.poc.agent.graph.nodes;

import org.langchain4j.graph.Node;
import org.springframework.stereotype.Component;
import com.poc.agent.graph.ArchState;

@Component
public class FinalNode implements Node<ArchState> {
    @Override
    public ArchState apply(ArchState state) { return state; }
}
