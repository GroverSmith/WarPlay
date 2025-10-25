-- Migration to add image_url column to crusades table
ALTER TABLE crusades ADD COLUMN image_url TEXT;
