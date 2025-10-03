package com.banco.turnmanagement.batch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job cleanupJob;
    
    @PostMapping("/run-cleanup")
    public ResponseEntity<String> runCleanupManually() {
        try {
            logger.info("Ejecutando limpieza manual...");
            
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(cleanupJob, params);
            
            String message = String.format(
                "Job ejecutado: %s\nStatus: %s\nItems eliminados: %d",
                execution.getJobInstance().getJobName(),
                execution.getStatus(),
                execution.getStepExecutions().iterator().next().getWriteCount()
            );
            
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            logger.error("Error ejecutando limpieza", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
}
