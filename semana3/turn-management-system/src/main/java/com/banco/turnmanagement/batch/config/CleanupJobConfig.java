package com.banco.turnmanagement.batch.config;

import com.banco.turnmanagement.batch.tasklets.CleanupTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CleanupJobConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CleanupJobConfig.class);
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Autowired
    private CleanupTasklet cleanupTasklet;
    
    @Bean
    public Job cleanupJob() {
        logger.info("Configurando Cleanup Job");
        return new JobBuilder("cleanupJob", jobRepository)
                .start(cleanupOldTurnsStep())
                .build();
    }
    
    @Bean
    public Step cleanupOldTurnsStep() {
        return new StepBuilder("cleanupOldTurnsStep", jobRepository)
                .tasklet(cleanupTasklet, transactionManager)
                .build();
    }
}