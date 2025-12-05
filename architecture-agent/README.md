# architecture-agent

Spring Boot agent (LangGraph4j) that orchestrates architecture extraction, analysis, vector search, compliance checks, and target architecture generation.

Set environment variables:
- MCP_BASE (default http://localhost:8080/tool)
- OPENAI_API_KEY

Build & run:
mvn -DskipTests clean package
MCP_BASE=http://localhost:8080/tool OPENAI_API_KEY=sk-... mvn spring-boot:run
