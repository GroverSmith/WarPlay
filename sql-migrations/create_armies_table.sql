-- Create armies table
CREATE TABLE IF NOT EXISTS armies (
    id BIGSERIAL PRIMARY KEY,
    force_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    faction VARCHAR(100),
    detachment VARCHAR(100),
    mfm_version VARCHAR(20),
    points INTEGER NOT NULL,
    army_type VARCHAR(20) NOT NULL CHECK (army_type IN ('paste', 'build')),
    army_text TEXT,
    notes TEXT,
    created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_timestamp TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_armies_force_id FOREIGN KEY (force_id) REFERENCES forces(id),
    CONSTRAINT fk_armies_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    
    -- Check constraints
    CONSTRAINT chk_armies_points CHECK (points >= 0),
    CONSTRAINT chk_armies_name_length CHECK (LENGTH(name) > 0)
);

-- Create army_units junction table for build mode armies
CREATE TABLE IF NOT EXISTS army_units (
    id BIGSERIAL PRIMARY KEY,
    army_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_timestamp TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_army_units_army_id FOREIGN KEY (army_id) REFERENCES armies(id),
    CONSTRAINT fk_army_units_unit_id FOREIGN KEY (unit_id) REFERENCES units(id),
    
    -- Unique constraint to prevent duplicate unit assignments
    CONSTRAINT uk_army_units_army_unit UNIQUE (army_id, unit_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_armies_force_id ON armies(force_id);
CREATE INDEX IF NOT EXISTS idx_armies_user_id ON armies(user_id);
CREATE INDEX IF NOT EXISTS idx_armies_deleted_timestamp ON armies(deleted_timestamp);
CREATE INDEX IF NOT EXISTS idx_armies_created_timestamp ON armies(created_timestamp);
CREATE INDEX IF NOT EXISTS idx_armies_army_type ON armies(army_type);

CREATE INDEX IF NOT EXISTS idx_army_units_army_id ON army_units(army_id);
CREATE INDEX IF NOT EXISTS idx_army_units_unit_id ON army_units(unit_id);
CREATE INDEX IF NOT EXISTS idx_army_units_deleted_timestamp ON army_units(deleted_timestamp);

-- Add comments for documentation
COMMENT ON TABLE armies IS 'Army lists created by users for their forces';
COMMENT ON COLUMN armies.force_id IS 'Reference to the force this army belongs to';
COMMENT ON COLUMN armies.user_id IS 'Reference to the user who owns this army';
COMMENT ON COLUMN armies.name IS 'Display name for the army list';
COMMENT ON COLUMN armies.faction IS 'Faction this army belongs to';
COMMENT ON COLUMN armies.detachment IS 'Detachment used by this army';
COMMENT ON COLUMN armies.mfm_version IS 'MFM version used for this army';
COMMENT ON COLUMN armies.points IS 'Total points cost of the army';
COMMENT ON COLUMN armies.army_type IS 'Type of army: paste (from external builder) or build (from force units)';
COMMENT ON COLUMN armies.army_text IS 'Raw army list text for paste mode armies';
COMMENT ON COLUMN armies.notes IS 'Additional notes about this army list';
COMMENT ON COLUMN armies.created_timestamp IS 'When the army was first created';
COMMENT ON COLUMN armies.updated_timestamp IS 'When the army was last modified';
COMMENT ON COLUMN armies.deleted_timestamp IS 'Soft delete timestamp (NULL if not deleted)';

COMMENT ON TABLE army_units IS 'Junction table linking armies to units for build mode armies';
COMMENT ON COLUMN army_units.army_id IS 'Reference to the army';
COMMENT ON COLUMN army_units.unit_id IS 'Reference to the unit included in this army';
COMMENT ON COLUMN army_units.created_timestamp IS 'When the unit was added to the army';
COMMENT ON COLUMN army_units.deleted_timestamp IS 'Soft delete timestamp (NULL if not deleted)';
