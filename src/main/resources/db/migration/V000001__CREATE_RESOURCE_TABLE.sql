CREATE TABLE IF NOT EXISTS resource
(
  id            SERIAL   NOT NULL,
  name          TEXT     NOT NULL      UNIQUE,
  path          TEXT     NOT NULL,
  hash          TEXT,

  CONSTRAINT pk_resource PRIMARY KEY (id)
);
