package com.poc.mcp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.poc.mcp.model.Components;
import com.poc.mcp.model.MermaidResult;

@RestController
@RequestMapping("/tool/mermaid")
public class MermaidController {

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Components comps) {
        StringBuilder sb = new StringBuilder("flowchart LR\n");
        if (comps.services != null) {
            for (Components.Node n : comps.services) {
                String id = safeId(n.id);
                sb.append("  ").append(id).append("[\"").append(n.label).append("\"]\n");
            }
        }
        if (comps.databases != null) {
            for (Components.Node n : comps.databases) {
                String id = safeId(n.id);
                sb.append("  ").append(id).append("[\"").append(n.label).append("\"]\n");
            }
        }
        if (comps.connections != null) {
            for (Components.Edge e : comps.connections) {
                sb.append("  ").append(safeId(e.from)).append(" --> ").append(safeId(e.to)).append("\n");
            }
        }
        MermaidResult r = new MermaidResult();
        r.mermaid = sb.toString();
        return ResponseEntity.ok(r);
    }

    private String safeId(String s) {
        if (s == null) return "n";
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
