-- Add mfm_version column to forces table
-- This column stores the MFM (Matched Play Guide) version used for this force

ALTER TABLE forces 
ADD COLUMN mfm_version VARCHAR(20);

-- Add comment to document the column
COMMENT ON COLUMN forces.mfm_version IS 'MFM (Matched Play Guide) version used for this force (e.g., "1.0", "1.1", "preset")';
