REM Run this script to create database tables and inserts
REM This is for DEVELOPMENT ONLY
REM  
 
 
REM DROP AND CREATE DATABASE 
"c:\Program Files\PostgreSQL\9.3\bin\psql.exe" -h localhost -p 5432 -U postgres -W postgres < createDB.sql 
 
REM CREATE TABLES, IMPORT BASE DATA
"c:\Program Files\PostgreSQL\9.3\bin\psql.exe" -h localhost -p 5432 -U postgres -W openstyle < setup.sql 

