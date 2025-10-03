package com.banco.turnmanagement.batch.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job cleanupJob;
    
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyCleanup() {
        try {
            logger.info("⏰ Iniciando limpieza programada...");
            
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(cleanupJob, params);
            
            logger.info("Limpieza completada con status: {}", execution.getStatus());
            
        } catch (Exception e) {
            logger.error("Error ejecutando limpieza programada", e);
        }
    }
    
    
    @Scheduled(fixedRate = 120000)
    public void runTestCleanup() {
        logger.info("Ejecutando limpieza de prueba...");
        runDailyCleanup();
    }
}
