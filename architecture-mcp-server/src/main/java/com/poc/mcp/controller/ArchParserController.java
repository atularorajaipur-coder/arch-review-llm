package com.poc.mcp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.mcp.service.VisionService;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/tool/arch")
public class ArchParserController {

    private final VisionService visionService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ArchParserController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping(value = "/parse", consumes = {"multipart/form-data"})
    public ResponseEntity<?> parse(@RequestPart("file") MultipartFile file,
                                   @RequestParam(value = "hint", required = false) String hint) throws Exception {
        byte[] bytes = file.getBytes();
        String b64 = Base64.getEncoder().encodeToString(bytes);

        String prompt = String.format(
            "You are an expert cloud & systems architect. Given the uploaded architecture diagram, extract the structure. Return STRICT JSON in the format: {\n  \"services\": [{\"id\":\"svc1\",\"name\":\"Card Service\",\"type\":\"service\",\"properties\":{} }],\n  \"databases\":[{\"id\":\"db1\",\"name\":\"CardsDB\",\"type\":\"database\",\"properties\":{\"encrypted\":false}}],\n  \"external\":[],\n  \"connections\":[{\"from\":\"svc1\",\"to\":\"db1\",\"protocol\":\"jdbc\"}]\n}\nOnly return JSON. Hints: %s", hint==null?"":hint);

        String content = visionService.extractFromImageBase64(b64, prompt);

        try {
            Map parsed = mapper.readValue(content, Map.class);
            return ResponseEntity.ok(parsed);
        } catch (Exception ex) {
            return ResponseEntity.ok(Map.of("raw", content, "note", "Model output not strict JSON"));
        }
    }
}
