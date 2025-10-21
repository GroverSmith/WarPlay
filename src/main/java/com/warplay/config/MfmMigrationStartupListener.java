package com.warplay.config;

import com.warplay.service.MfmMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class MfmMigrationStartupListener {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmMigrationStartupListener.class);
    
    @Autowired
    private MfmMigrationService mfmMigrationService;
    
    /**
     * Run MFM migration after application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(100) // Run after other startup tasks
    public void onApplicationReady() {
        logger.info("Application ready - starting MFM migration");
        
        try {
            mfmMigrationService.runMigration();
        } catch (Exception e) {
            logger.error("Error during MFM migration at startup", e);
        }
    }
}
