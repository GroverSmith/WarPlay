-- Migration: Add force_type column to forces table
-- This script handles adding the force_type column with proper default values

-- Step 1: Add the column as nullable first
ALTER TABLE forces ADD COLUMN force_type VARCHAR(20);

-- Step 2: Update existing records to have a default value
UPDATE forces SET force_type = 'basic' WHERE force_type IS NULL;

-- Step 3: Make the column non-nullable
ALTER TABLE forces ALTER COLUMN force_type SET NOT NULL;

-- Step 4: Add a check constraint to ensure only valid values
ALTER TABLE forces ADD CONSTRAINT check_force_type 
    CHECK (force_type IN ('basic', 'crusade'));
