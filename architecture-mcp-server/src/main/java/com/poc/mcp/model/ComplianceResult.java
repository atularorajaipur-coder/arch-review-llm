package com.poc.mcp.model;

import java.util.List;

public class ComplianceResult {
    public int risk_score;
    public List<Issue> issues;
    public List<String> recommendations;

    public static class Issue {
        public String issue;
        public String severity;
        public String component;
    }
}
