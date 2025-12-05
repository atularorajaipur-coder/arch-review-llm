package com.poc.mcp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pgvector.PgVector;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/tool/patterns")
public class PatternController {

    @Value("${postgres.jdbc}")
    private String jdbcUrl;

    @Value("${postgres.user}")
    private String pgUser;

    @Value("${postgres.pass}")
    private String pgPass;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/ingest")
    public ResponseEntity<?> ingest(@RequestBody Map<String,Object> body) throws Exception {
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        List<Double> embedding = (List<Double>) body.get("embedding");
        float[] emb = new float[embedding.size()];
        for (int i=0;i<embedding.size();i++) emb[i] = embedding.get(i).floatValue();

        try (Connection c = DriverManager.getConnection(jdbcUrl, pgUser, pgPass)) {
            String sql = "INSERT INTO ref_architectures (title, description, metadata, embedding) VALUES (?,?,?::jsonb, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, mapper.writeValueAsString(body.getOrDefault("metadata", Map.of())));
                ps.setObject(4, new PgVector(emb));
                ps.executeUpdate();
            }
        }
        return ResponseEntity.ok(Map.of("status","ok"));
    }

    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody Map<String,Object> body) throws Exception {
        List<Double> embedding = (List<Double>) body.get("embedding");
        int k = (int) body.getOrDefault("k", 3);
        float[] emb = new float[embedding.size()];
        for (int i=0;i<embedding.size();i++) emb[i] = embedding.get(i).floatValue();

        List<Map<String,Object>> out = new ArrayList<>();
        String sql = "SELECT id, title, description, metadata, embedding <-> ? AS distance FROM ref_architectures ORDER BY distance LIMIT ?";

        try (Connection c = DriverManager.getConnection(jdbcUrl, pgUser, pgPass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, new PgVector(emb));
            ps.setInt(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> r = new HashMap<>();
                    r.put("id", rs.getInt("id"));
                    r.put("title", rs.getString("title"));
                    r.put("description", rs.getString("description"));
                    r.put("metadata", mapper.readValue(rs.getString("metadata"), Map.class));
                    r.put("distance", rs.getDouble("distance"));
                    out.add(r);
                }
            }
        }
        return ResponseEntity.ok(Map.of("matches", out));
    }
}
