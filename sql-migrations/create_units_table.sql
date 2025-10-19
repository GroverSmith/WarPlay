-- Create units table
CREATE TABLE IF NOT EXISTS units (
    id BIGSERIAL PRIMARY KEY,
    force_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    data_sheet VARCHAR(200) NOT NULL,
    model_count INTEGER NOT NULL,
    unit_type VARCHAR(50),
    points INTEGER,
    crusade_points INTEGER,
    wargear TEXT,
    enhancements TEXT,
    relics TEXT,
    battle_traits TEXT,
    battle_scars TEXT,
    battle_count INTEGER,
    xp INTEGER,
    kill_count INTEGER,
    times_killed INTEGER,
    description TEXT,
    notes TEXT,
    notable_history TEXT,
    mfm_version VARCHAR(20),
    rank VARCHAR(50),
    image_url TEXT,
    created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_timestamp TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_units_force_id FOREIGN KEY (force_id) REFERENCES forces(id),
    CONSTRAINT fk_units_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    
    -- Check constraints
    CONSTRAINT chk_units_model_count CHECK (model_count > 0),
    CONSTRAINT chk_units_points CHECK (points IS NULL OR points >= 0),
    CONSTRAINT chk_units_crusade_points CHECK (crusade_points IS NULL OR crusade_points >= 0),
    CONSTRAINT chk_units_battle_count CHECK (battle_count IS NULL OR battle_count >= 0),
    CONSTRAINT chk_units_xp CHECK (xp IS NULL OR xp >= 0),
    CONSTRAINT chk_units_kill_count CHECK (kill_count IS NULL OR kill_count >= 0),
    CONSTRAINT chk_units_times_killed CHECK (times_killed IS NULL OR times_killed >= 0)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_units_force_id ON units(force_id);
CREATE INDEX IF NOT EXISTS idx_units_user_id ON units(user_id);
CREATE INDEX IF NOT EXISTS idx_units_deleted_timestamp ON units(deleted_timestamp);
CREATE INDEX IF NOT EXISTS idx_units_created_timestamp ON units(created_timestamp);

-- Add comments for documentation
COMMENT ON TABLE units IS 'Units belonging to forces in the WarPlay system';
COMMENT ON COLUMN units.force_id IS 'Reference to the force this unit belongs to';
COMMENT ON COLUMN units.user_id IS 'Reference to the user who owns this unit';
COMMENT ON COLUMN units.name IS 'Display name for the unit';
COMMENT ON COLUMN units.data_sheet IS 'Official data sheet name from MFM or custom';
COMMENT ON COLUMN units.model_count IS 'Number of models in this unit';
COMMENT ON COLUMN units.unit_type IS 'Battlefield role (HQ, Troops, Elites, etc.)';
COMMENT ON COLUMN units.points IS 'Points cost of the unit';
COMMENT ON COLUMN units.crusade_points IS 'Crusade points cost of the unit';
COMMENT ON COLUMN units.wargear IS 'Equipment and weapons carried by the unit';
COMMENT ON COLUMN units.enhancements IS 'Special enhancements or upgrades';
COMMENT ON COLUMN units.relics IS 'Relics carried by the unit';
COMMENT ON COLUMN units.battle_traits IS 'Battle traits gained by the unit';
COMMENT ON COLUMN units.battle_scars IS 'Battle scars suffered by the unit';
COMMENT ON COLUMN units.battle_count IS 'Number of battles the unit has fought';
COMMENT ON COLUMN units.xp IS 'Experience points earned by the unit';
COMMENT ON COLUMN units.kill_count IS 'Number of enemy units killed by this unit';
COMMENT ON COLUMN units.times_killed IS 'Number of times this unit has been killed';
COMMENT ON COLUMN units.description IS 'Flavor text and unit description';
COMMENT ON COLUMN units.notes IS 'Additional notes about the unit';
COMMENT ON COLUMN units.notable_history IS 'Notable events in the unit history';
COMMENT ON COLUMN units.mfm_version IS 'MFM version used for this unit data';
COMMENT ON COLUMN units.rank IS 'Current rank/experience level of the unit';
COMMENT ON COLUMN units.image_url IS 'URL to the unit image/picture';
COMMENT ON COLUMN units.created_timestamp IS 'When the unit was first created';
COMMENT ON COLUMN units.updated_timestamp IS 'When the unit was last modified';
COMMENT ON COLUMN units.deleted_timestamp IS 'Soft delete timestamp (NULL if not deleted)';
