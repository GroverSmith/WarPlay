-- Add image_url column to armies table
-- This migration adds support for army images

ALTER TABLE armies ADD COLUMN image_url TEXT;

-- Add comment to document the column
COMMENT ON COLUMN armies.image_url IS 'URL or path to the army image file';
