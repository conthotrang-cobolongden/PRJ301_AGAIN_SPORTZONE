-- =============================================================================
-- SportZoneDB — Full init (schema + seed) in one run
-- Open in SSMS / Azure Data Studio and Execute (F5).
-- Requires permission to CREATE DATABASE on the SQL Server instance.
-- =============================================================================

:r "01_SportZoneDB_Schema.sql"
:r "02_SportZoneDB_Seed.sql"

-- NOTE:
-- SQLCMD mode must be enabled for :r (SSMS: Query > SQLCMD Mode).
-- If you cannot use SQLCMD mode, run 01 then 02 manually in order.
