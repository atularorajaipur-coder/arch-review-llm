package com.poc.mcp.model;

import java.util.List;

public class Components {

    public static class Node {
        public String id;
        public String type;
        public String label;
        public Object properties;
    }

    public static class Edge {
        public String from;
        public String to;
        public String protocol;
    }

    public List<Node> services;
    public List<Node> databases;
    public List<Node> external;
    public List<Edge> connections;
}
