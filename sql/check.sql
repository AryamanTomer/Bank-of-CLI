-- Run this on the "postgres (admin)" connection to see what exists.
SELECT datname
FROM pg_database
WHERE datistemplate = false
ORDER BY 1;

SELECT current_database();

SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY 1;
