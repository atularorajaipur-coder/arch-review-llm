-- Create extension and table
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS ref_architectures (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  metadata JSONB DEFAULT '{}'::jsonb,
  embedding vector(1536)
);
