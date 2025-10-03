package com.banco.turnmanagement.batch;

import com.banco.turnmanagement.batch.tasklets.CleanupTasklet;
import com.banco.turnmanagement.turns.Turn;
import com.banco.turnmanagement.turns.TurnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanupTaskletTest {
    
    @Mock
    private TurnRepository turnRepository;
    
    @Mock
    private StepContribution stepContribution;
    
    @Mock
    private ChunkContext chunkContext;
    
    @InjectMocks
    private CleanupTasklet cleanupTasklet;
    
    private Turn oldTurn1;
    private Turn oldTurn2;
    
    @BeforeEach
    void setUp() {
        LocalDateTime oldDate = LocalDateTime.now().minusDays(35);
        
        oldTurn1 = new Turn(
            1L, 100L, Turn.ServiceType.CAJA, "C001", 
            Turn.TurnStatus.COMPLETADO, oldDate, oldDate
        );
        
        oldTurn2 = new Turn(
            2L, 101L, Turn.ServiceType.EJECUTIVO, "E001", 
            Turn.TurnStatus.COMPLETADO, oldDate, oldDate
        );
    }
    
    @Test
    void testExecute_WithOldTurns_DeletesSuccessfully() throws Exception {
        // Arrange
        List<Turn> oldTurns = Arrays.asList(oldTurn1, oldTurn2);
        when(turnRepository.findCompletedBefore(any(LocalDateTime.class))).thenReturn(oldTurns);
        
        // Act
        RepeatStatus status = cleanupTasklet.execute(stepContribution, chunkContext);
        
        // Assert
        assertEquals(RepeatStatus.FINISHED, status);
        verify(turnRepository, times(1)).findCompletedBefore(any(LocalDateTime.class));
        verify(turnRepository, times(1)).deleteAll(oldTurns);
        verify(stepContribution, times(1)).incrementWriteCount(2);
    }
    
    @Test
    void testExecute_WithNoOldTurns_DoesNotDelete() throws Exception {
        // Arrange
        when(turnRepository.findCompletedBefore(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        // Act
        RepeatStatus status = cleanupTasklet.execute(stepContribution, chunkContext);
        
        // Assert
        assertEquals(RepeatStatus.FINISHED, status);
        verify(turnRepository, times(1)).findCompletedBefore(any(LocalDateTime.class));
        verify(turnRepository, never()).deleteAll(any());
        verify(stepContribution, times(1)).incrementWriteCount(0);
    }
    
    @Test
    void testExecute_CalculatesCutoffDateCorrectly() throws Exception {
        // Arrange
        when(turnRepository.findCompletedBefore(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        LocalDateTime beforeExecution = LocalDateTime.now().minusDays(30);
        
        // Act
        cleanupTasklet.execute(stepContribution, chunkContext);
        
        // Assert
        verify(turnRepository).findCompletedBefore(argThat(cutoffDate -> {
            // Verificar que la fecha de corte sea aproximadamente hace 30 días
            long minutesDiff = Math.abs(
                java.time.Duration.between(beforeExecution, cutoffDate).toMinutes()
            );
            return minutesDiff < 5; // Tolerancia de 5 minutos
        }));
    }
    
    @Test
    void testExecute_AlwaysReturnsFinished() throws Exception {
        // Arrange
        when(turnRepository.findCompletedBefore(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(oldTurn1));
        
        // Act
        RepeatStatus status = cleanupTasklet.execute(stepContribution, chunkContext);
        
        // Assert
        assertEquals(RepeatStatus.FINISHED, status);
    }
}
