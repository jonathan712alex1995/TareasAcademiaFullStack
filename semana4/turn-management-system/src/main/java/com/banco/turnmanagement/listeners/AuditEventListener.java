package com.banco.turnmanagement.listeners;


import com.banco.turnmanagement.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

@Component
public class AuditEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Simulamos una cola de auditoría en memoria
    private final Queue<AuditRecord> auditLog = new ConcurrentLinkedQueue<>();
    
    @EventListener
    public void handleTurnCreated(TurnCreated event) {
        AuditRecord record = new AuditRecord(
            "TURN_CREATED",
            "Turn " + event.turnNumber() + " created for customer " + event.customerId(),
            event.createdAt().format(formatter),
            "SYSTEM"
        );
        
        auditLog.offer(record);
        logger.info("AUDIT: {}", record);
        
        // Registrar métricas
        recordMetric("turns_created", event.serviceType());
    }
    
    @EventListener
    public void handleTurnCalled(TurnCalled event) {
        AuditRecord record = new AuditRecord(
            "TURN_CALLED",
            "Turn " + event.turnNumber() + " called by cashier " + event.cashierName() + 
            " (ID: " + event.cashierId() + ")",
            event.calledAt().format(formatter),
            "CASHIER_" + event.cashierId()
        );
        
        auditLog.offer(record);
        logger.info("AUDIT: {}", record);
        
        // Registrar métricas de tiempo de espera
        recordWaitingTimeMetric(event.turnId());
    }
    
    @EventListener
    public void handleTurnCompleted(TurnCompleted event) {
        AuditRecord record = new AuditRecord(
            "TURN_COMPLETED",
            "Turn " + event.turnNumber() + " completed" +
            (event.cashierId() != null ? " by cashier " + event.cashierId() : ""),
            event.completedAt().format(formatter),
            event.cashierId() != null ? "CASHIER_" + event.cashierId() : "SYSTEM"
        );
        
        auditLog.offer(record);
        logger.info("AUDIT: {}", record);
        
        // Registrar métricas de atención
        recordServiceTimeMetric(event.turnId());
        recordMetric("turns_completed", null);
    }
    
    @EventListener
    public void handleCashierAvailable(CashierAvailable event) {
        AuditRecord record = new AuditRecord(
            "CASHIER_AVAILABLE",
            "Cashier " + event.cashierName() + " (ID: " + event.cashierId() + 
            ") became available for " + event.serviceType(),
            LocalDateTime.now().format(formatter),
            "SYSTEM"
        );
        
        auditLog.offer(record);
        logger.info("AUDIT: {}", record);
        
        // Registrar métricas de disponibilidad
        recordMetric("cashier_available", event.serviceType());
    }
    
    // Métodos auxiliares para métricas y auditoría
    private void recordMetric(String metricName, String category) {
        String fullMetric = category != null ? metricName + "_" + category : metricName;
        logger.debug("METRIC: {} incremented", fullMetric);
        
        // En producción, esto se enviaría a sistemas como Prometheus, Grafana, etc.
    }
    
    private void recordWaitingTimeMetric(Long turnId) {
        logger.debug("METRIC: Waiting time recorded for turn {}", turnId);
        // Calcular tiempo desde creación hasta llamada
    }
    
    private void recordServiceTimeMetric(Long turnId) {
        logger.debug("METRIC: Service time recorded for turn {}", turnId);
        // Calcular tiempo desde llamada hasta completado
    }
    
    // Método para obtener registros de auditoría (útil para APIs de reporting)
    public Queue<AuditRecord> getAuditLog() {
        return new ConcurrentLinkedQueue<>(auditLog);
    }
    
    // Método para limpiar logs antiguos
    public void cleanOldRecords(int maxRecords) {
        while (auditLog.size() > maxRecords) {
            auditLog.poll();
        }
        logger.debug("Audit log cleaned, keeping {} records", maxRecords);
    }
    
    // Clase interna para registros de auditoría
    public static class AuditRecord {
        private final String eventType;
        private final String description;
        private final String timestamp;
        private final String actor;
        
        public AuditRecord(String eventType, String description, String timestamp, String actor) {
            this.eventType = eventType;
            this.description = description;
            this.timestamp = timestamp;
            this.actor = actor;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s (by %s)", timestamp, eventType, description, actor);
        }
        
        // Getters
        public String getEventType() { return eventType; }
        public String getDescription() { return description; }
        public String getTimestamp() { return timestamp; }
        public String getActor() { return actor; }
    }
}