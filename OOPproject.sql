--
-- PostgreSQL database cluster dump
--

-- Started on 2024-12-24 16:16:03

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE postgres;
ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:gPzMHmx9wPpJRpSqvES4Rg==$gVh7qbpUEc7XTGeaHSHTrzrJWcSojwC6LmJgPgITvY4=:CkWFlkScuv9+V1DjnAb+WDd7iCUZ+nxy7Du/O9u6uP8=';

--
-- User Configurations
--








-- Completed on 2024-12-24 16:16:03

--
-- PostgreSQL database cluster dump complete
--

