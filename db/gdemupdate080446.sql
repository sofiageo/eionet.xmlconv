-- Use to update previous version of GDEM database


-- Add SCHEMA_URL field to T_UPL_SCHEMA table


 ALTER TABLE `T_UPL_SCHEMA` ADD COLUMN `SCHEMA_URL` VARCHAR(255) NOT NULL default '';