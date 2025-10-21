package com.warplay.service;

import com.warplay.entity.MfmVersion;
import com.warplay.repository.MfmVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MfmVersionManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmVersionManagementService.class);
    
    @Autowired
    private MfmVersionRepository mfmVersionRepository;
    
    /**
     * Deactivate a specific MFM version
     */
    @Transactional
    public void deactivateVersion(String version) {
        Optional<MfmVersion> versionOpt = mfmVersionRepository.findByVersion(version);
        if (versionOpt.isPresent()) {
            MfmVersion mfmVersion = versionOpt.get();
            mfmVersion.setIsActive(false);
            mfmVersionRepository.save(mfmVersion);
            logger.info("Deactivated MFM version: {}", version);
        } else {
            logger.warn("MFM version not found: {}", version);
        }
    }
    
    /**
     * Activate a specific MFM version
     */
    @Transactional
    public void activateVersion(String version) {
        Optional<MfmVersion> versionOpt = mfmVersionRepository.findByVersion(version);
        if (versionOpt.isPresent()) {
            MfmVersion mfmVersion = versionOpt.get();
            mfmVersion.setIsActive(true);
            mfmVersionRepository.save(mfmVersion);
            logger.info("Activated MFM version: {}", version);
        } else {
            logger.warn("MFM version not found: {}", version);
        }
    }
    
    /**
     * Deactivate all versions except the specified one
     */
    @Transactional
    public void deactivateAllExcept(String keepActiveVersion) {
        List<MfmVersion> allVersions = mfmVersionRepository.findAll();
        for (MfmVersion version : allVersions) {
            if (!version.getVersion().equals(keepActiveVersion)) {
                version.setIsActive(false);
                mfmVersionRepository.save(version);
                logger.info("Deactivated MFM version: {}", version.getVersion());
            }
        }
        logger.info("Deactivated all versions except: {}", keepActiveVersion);
    }
    
    /**
     * Get all active versions
     */
    public List<MfmVersion> getActiveVersions() {
        return mfmVersionRepository.findByIsActiveTrueOrderByVersionAsc();
    }
    
    /**
     * Get all versions (active and inactive)
     */
    public List<MfmVersion> getAllVersions() {
        return mfmVersionRepository.findAllByOrderByVersionAsc();
    }
    
    /**
     * Get the latest active version
     */
    public Optional<MfmVersion> getLatestActiveVersion() {
        return mfmVersionRepository.findByIsLatestTrueAndIsActiveTrue();
    }
    
    /**
     * Check if a version is active
     */
    public boolean isVersionActive(String version) {
        Optional<MfmVersion> versionOpt = mfmVersionRepository.findByVersion(version);
        return versionOpt.map(MfmVersion::getIsActive).orElse(false);
    }
    
    /**
     * Get version status summary
     */
    public String getVersionStatusSummary() {
        List<MfmVersion> allVersions = mfmVersionRepository.findAllByOrderByVersionAsc();
        StringBuilder summary = new StringBuilder();
        
        summary.append("MFM Version Status Summary:\n");
        summary.append("==========================\n");
        
        for (MfmVersion version : allVersions) {
            String status = version.getIsActive() ? "ACTIVE" : "INACTIVE";
            String latest = version.getIsLatest() ? " (LATEST)" : "";
            summary.append(String.format("%s: %s%s\n", version.getVersion(), status, latest));
        }
        
        return summary.toString();
    }
}
