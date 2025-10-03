package com.banco.turnmanagement.turns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnServiceTest {
    
    @Mock
    private TurnRepository turnRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private TurnService turnService;
    
    private Turn testTurn;
    
    @BeforeEach
    void setUp() {
        testTurn = new Turn(
            1L,
            100L,
            Turn.ServiceType.CAJA,
            "C001",
            Turn.TurnStatus.ESPERA,
            LocalDateTime.now(),
            null
        );
    }
    
    @Test
    void testCreateTurn_Success() {
        // Arrange
        when(turnRepository.save(any(Turn.class))).thenReturn(testTurn);
        
        // Act
        Turn resultado = turnService.createTurn(100L, Turn.ServiceType.CAJA);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(Turn.TurnStatus.ESPERA, resultado.getStatus());
        verify(turnRepository, times(1)).save(any(Turn.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }
    
    @Test
    void testGetWaitingTurns() {
        // Arrange
        Turn turn1 = new Turn(1L, 100L, Turn.ServiceType.CAJA, "C001", Turn.TurnStatus.ESPERA, LocalDateTime.now(), null);
        Turn turn2 = new Turn(2L, 101L, Turn.ServiceType.CAJA, "C002", Turn.TurnStatus.ESPERA, LocalDateTime.now(), null);
        
        when(turnRepository.findByStatusOrderByCreatedAtAsc(Turn.TurnStatus.ESPERA))
            .thenReturn(Arrays.asList(turn1, turn2));
        
        // Act
        List<Turn> resultado = turnService.getWaitingTurns();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(turnRepository, times(1)).findByStatusOrderByCreatedAtAsc(Turn.TurnStatus.ESPERA);
    }
    
    @Test
    void testCallNextTurn_Success() {
        // Arrange
        Turn waitingTurn = new Turn(1L, 100L, Turn.ServiceType.CAJA, "C001", Turn.TurnStatus.ESPERA, LocalDateTime.now(), null);
        when(turnRepository.findByStatusOrderByCreatedAtAsc(Turn.TurnStatus.ESPERA))
            .thenReturn(Arrays.asList(waitingTurn));
        
        Turn calledTurn = new Turn(1L, 100L, Turn.ServiceType.CAJA, "C001", Turn.TurnStatus.LLAMADO, LocalDateTime.now(), LocalDateTime.now());
        when(turnRepository.save(any(Turn.class))).thenReturn(calledTurn);
        
        // Act
        Optional<Turn> resultado = turnService.callNextTurn(1L, "Juan Cajero");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(Turn.TurnStatus.LLAMADO, resultado.get().getStatus());
        assertNotNull(resultado.get().getCalledAt());
        verify(turnRepository, times(1)).save(any(Turn.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }
    
    @Test
    void testCallNextTurn_NoWaitingTurns() {
        // Arrange
        when(turnRepository.findByStatusOrderByCreatedAtAsc(Turn.TurnStatus.ESPERA))
            .thenReturn(Arrays.asList());
        
        // Act
        Optional<Turn> resultado = turnService.callNextTurn(1L, "Juan Cajero");
        
        // Assert
        assertFalse(resultado.isPresent());
        verify(turnRepository, never()).save(any(Turn.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
    
    @Test
    void testCompleteTurn_Success() {
        // Arrange
        when(turnRepository.findById(1L)).thenReturn(Optional.of(testTurn));
        
        Turn completedTurn = new Turn(
            1L, 100L, Turn.ServiceType.CAJA, "C001", 
            Turn.TurnStatus.COMPLETADO, LocalDateTime.now(), LocalDateTime.now()
        );
        when(turnRepository.save(any(Turn.class))).thenReturn(completedTurn);
        
        // Act
        Turn resultado = turnService.completeTurn(1L);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(Turn.TurnStatus.COMPLETADO, resultado.getStatus());
        verify(turnRepository, times(1)).save(any(Turn.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }
    
    @Test
    void testCompleteTurn_TurnNotFound() {
        // Arrange
        when(turnRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            turnService.completeTurn(999L);
        });
        
        verify(turnRepository, never()).save(any(Turn.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
    
    @Test
    void testGenerateTurnNumber_FormatoCorrecto() {
        // Arrange
        when(turnRepository.save(any(Turn.class))).thenReturn(testTurn);
        
        // Act
        Turn resultado = turnService.createTurn(100L, Turn.ServiceType.CAJA);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getTurnNumber().startsWith("C"));
    }
    
    @Test
    void testGenerateTurnNumber_DiferenteParaEjecutivo() {
        // Arrange
        Turn executiveTurn = new Turn(
            1L, 100L, Turn.ServiceType.EJECUTIVO, "E001", 
            Turn.TurnStatus.ESPERA, LocalDateTime.now(), null
        );
        when(turnRepository.save(any(Turn.class))).thenReturn(executiveTurn);
        
        // Act
        Turn resultado = turnService.createTurn(100L, Turn.ServiceType.EJECUTIVO);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getTurnNumber().startsWith("E"));
    }
}
