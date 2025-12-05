package com.poc.agent.graph;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchState {
    public String businessReq;
    public String nfrReq;
    public byte[] archImage;

    public String extractedComponentsJson;
    public String analysisText;
    public String similarPatternsJson;
    public String complianceJson;
    public String targetArchitectureJson;
    public String mermaid;
}
