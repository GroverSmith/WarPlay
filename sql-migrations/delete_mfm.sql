-- Step 1: Delete enhancements (they reference detachments)
DELETE FROM mfm_enhancements
WHERE detachment_id IN (
    SELECT d.id FROM mfm_detachments d
    JOIN mfm_factions f ON d.faction_id = f.id
    WHERE f.mfm_version_id = (SELECT id FROM mfm_versions WHERE version = '3.2')
);

-- Step 2: Delete unit variants (they reference units)
DELETE FROM mfm_unit_variants
WHERE unit_id IN (
    SELECT u.id FROM mfm_units u
    JOIN mfm_factions f ON u.faction_id = f.id
    WHERE f.mfm_version_id = (SELECT id FROM mfm_versions WHERE version = '3.2')
);

-- Step 3: Delete detachments (they reference factions)
DELETE FROM mfm_detachments
WHERE faction_id IN (
    SELECT id FROM mfm_factions
    WHERE mfm_version_id = (SELECT id FROM mfm_versions WHERE version = '3.2')
);

-- Step 4: Delete units (they reference factions)
DELETE FROM mfm_units
WHERE faction_id IN (
    SELECT id FROM mfm_factions
    WHERE mfm_version_id = (SELECT id FROM mfm_versions WHERE version = '3.2')
);

-- Step 5: Delete factions (they reference mfm_version)
DELETE FROM mfm_factions
WHERE mfm_version_id = (SELECT id FROM mfm_versions WHERE version = '3.2');

-- Step 6: Finally delete the version itself
DELETE FROM mfm_versions WHERE version = '3.2';