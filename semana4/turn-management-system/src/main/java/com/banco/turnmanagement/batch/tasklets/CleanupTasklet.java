package com.banco.turnmanagement.batch.tasklets;

import com.banco.turnmanagement.turns.Turn;
import com.banco.turnmanagement.turns.TurnRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanupTasklet implements Tasklet {
    
    private static final Logger logger = LoggerFactory.getLogger(CleanupTasklet.class);
    
    @Autowired
    private TurnRepository turnRepository;
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        
        logger.info("Iniciando limpieza de turnos anteriores a: {}", cutoffDate);
        
        List<Turn> oldTurns = turnRepository.findCompletedBefore(cutoffDate);
        int deletedCount = oldTurns.size();
        
        if (deletedCount > 0) {
            turnRepository.deleteAll(oldTurns);
            logger.info("Limpieza completada: {} turnos eliminados", deletedCount);
        } else {
            logger.info("No hay turnos antiguos para eliminar");
        }
        
        
        contribution.incrementWriteCount(deletedCount);
        
        return RepeatStatus.FINISHED;
    }
}