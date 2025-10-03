package com.banco.turnmanagement.turns;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {
    
    List<Turn> findByStatusOrderByCreatedAtAsc(Turn.TurnStatus status);
    
    Optional<Turn> findByTurnNumber(String turnNumber);
    
    @Query("SELECT t FROM Turn t WHERE t.status IN ('ESPERA', 'LLAMADO') ORDER BY t.createdAt ASC")
    List<Turn> findActiveTurns();
    
    long countByStatus(Turn.TurnStatus status);
    
    @Query("SELECT t FROM Turn t WHERE t.status = 'COMPLETED' AND t.createdAt < :cutoffDate")
    List<Turn> findCompletedBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
