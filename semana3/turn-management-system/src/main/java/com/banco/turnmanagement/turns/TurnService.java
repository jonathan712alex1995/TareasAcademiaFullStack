package com.banco.turnmanagement.turns;

import com.banco.turnmanagement.events.TurnCreated;
import com.banco.turnmanagement.events.TurnCalled;
import com.banco.turnmanagement.events.TurnCompleted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TurnService {
    
    @Autowired
    private TurnRepository turnRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public Turn createTurn(Long customerId, Turn.ServiceType serviceType) {
        String turnNumber = generateTurnNumber(serviceType);
        
        Turn turn = new Turn(
            null,
            customerId,
            serviceType,
            turnNumber,
            Turn.TurnStatus.ESPERA,
            LocalDateTime.now(),
            null
        );
        
        Turn savedTurn = turnRepository.save(turn);
        
        // Publicar evento
        TurnCreated event = new TurnCreated(
            savedTurn.getId(),
            customerId,
            turnNumber,
            serviceType.toString(),
            savedTurn.getCreatedAt()
        );
        eventPublisher.publishEvent(event);
        
        return savedTurn;
    }
    
    public List<Turn> getWaitingTurns() {
        return turnRepository.findByStatusOrderByCreatedAtAsc(Turn.TurnStatus.ESPERA);
    }
    
    public Optional<Turn> callNextTurn(Long cashierId, String cashierName) {
        List<Turn> waitingTurns = getWaitingTurns();
        if (waitingTurns.isEmpty()) {
            return Optional.empty();
        }
        
        Turn turn = waitingTurns.get(0);
        turn.setStatus(Turn.TurnStatus.LLAMADO);
        turn.setCalledAt(LocalDateTime.now());
        
        Turn savedTurn = turnRepository.save(turn);
        
        // Publicar evento
        TurnCalled event = new TurnCalled(
            savedTurn.getId(),
            savedTurn.getTurnNumber(),
            cashierId,
            cashierName,
            savedTurn.getCalledAt()
        );
        eventPublisher.publishEvent(event);
        
        return Optional.of(savedTurn);
    }
    
    public Turn completeTurn(Long turnId) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new IllegalArgumentException("Turn not found"));
        
        turn.setStatus(Turn.TurnStatus.COMPLETADO);
        Turn savedTurn = turnRepository.save(turn);
        
        // Publicar evento
        TurnCompleted event = new TurnCompleted(
            savedTurn.getId(),
            savedTurn.getTurnNumber(),
            null, // cashierId - lo agregaremos después
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
        
        return savedTurn;
    }
    
    private String generateTurnNumber(Turn.ServiceType serviceType) {
        String prefix = serviceType == Turn.ServiceType.CAJA ? "C" : "E";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return prefix + timestamp;
    }
}
