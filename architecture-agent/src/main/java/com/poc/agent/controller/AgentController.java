package com.poc.agent.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.langchain4j.graph.Graph;
import com.poc.agent.graph.ArchState;

@RestController
@RequestMapping("/api/arch")
public class AgentController {

    private final Graph<ArchState> graph;

    public AgentController(Graph<ArchState> graph) { this.graph = graph; }

    @PostMapping(value = "/analyze", consumes = {"multipart/form-data"})
    public ArchState analyzeMultipart(@RequestPart(value="file", required=false) MultipartFile file,
                                      @RequestParam(value="business", required=false) String business,
                                      @RequestParam(value="nfr", required=false) String nfr) throws Exception {
        ArchState state = ArchState.builder().businessReq(business).nfrReq(nfr).build();
        if (file != null && !file.isEmpty()) state.setArchImage(file.getBytes());
        return graph.run(state);
    }

    @PostMapping("/analyze-text")
    public ArchState analyzeText(@RequestParam String business, @RequestParam String nfr) {
        ArchState state = ArchState.builder().businessReq(business).nfrReq(nfr).build();
        return graph.run(state);
    }
}
