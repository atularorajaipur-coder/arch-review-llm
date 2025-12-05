# architecture-mcp-server

Spring Boot MCP-style tool server exposing endpoints for architecture POC.

Endpoints:
- POST /tool/arch/parse (multipart file)
- POST /tool/compliance/check
- POST /tool/improver/improve
- POST /tool/mermaid/generate
- POST /tool/patterns/ingest
- POST /tool/patterns/query

Set OPENAI_API_KEY env var and Postgres connection in application.yml before running.
