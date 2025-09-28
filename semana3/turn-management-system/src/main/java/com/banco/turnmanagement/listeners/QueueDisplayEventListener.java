package com.banco.turnmanagement.listeners;

import com.banco.turnmanagement.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class QueueDisplayEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(QueueDisplayEventListener.class);
    
    // Simulamos el estado de las pantallas de la sucursal
    private final Map<String, String> displayStatus = new ConcurrentHashMap<>();
    
    @EventListener
    public void handleTurnCreated(TurnCreated event) {
        logger.info("PANTALLA: Agregando turno {} a la cola de {}", 
            event.turnNumber(), event.serviceType());
        
        updateQueueDisplay(event.serviceType(), "NUEVO: " + event.turnNumber());
        
        // Actualizar contador de turnos en espera
        updateWaitingCounter(event.serviceType());
    }
    
    @EventListener
    public void handleTurnCalled(TurnCalled event) {
        logger.info("PANTALLA: Mostrando turno {} siendo atendido por {}", 
            event.turnNumber(), event.cashierName());
        
        // Mostrar en pantalla principal
        updateMainDisplay("AHORA ATENDIENDO: " + event.turnNumber() + 
                         " - Cajero: " + event.cashierName());
        
        // Actualizar estado del cajero en pantalla
        updateCashierStatus(event.cashierId(), "OCUPADO", event.turnNumber());
    }
    
    @EventListener
    public void handleTurnCompleted(TurnCompleted event) {
        logger.info("PANTALLA: Turno {} completado, liberando cajero", 
            event.turnNumber());
        
        // Limpiar pantalla del turno completado
        clearTurnFromDisplay(event.turnNumber());
        
        // Mostrar mensaje de turno completado temporalmente
        showTemporaryMessage("Turno " + event.turnNumber() + " completado");
    }
    
    @EventListener
    public void handleCashierAvailable(CashierAvailable event) {
        logger.info("PANTALLA: Cajero {} disponible para {}", 
            event.cashierName(), event.serviceType());
        
        // Actualizar estado del cajero en pantalla
        updateCashierStatus(event.cashierId(), "DISPONIBLE", null);
        
        // Mostrar en pantalla que hay cajero disponible
        updateServiceStatus(event.serviceType(), "CAJERO DISPONIBLE");
    }
    
    // Métodos simulados para actualizar pantallas
    private void updateQueueDisplay(String serviceType, String message) {
        displayStatus.put("QUEUE_" + serviceType, message);
        logger.debug("Cola {} actualizada: {}", serviceType, message);
    }
    
    private void updateMainDisplay(String message) {
        displayStatus.put("MAIN_DISPLAY", message);
        logger.debug("Pantalla principal: {}", message);
    }
    
    private void updateCashierStatus(Long cashierId, String status, String currentTurn) {
        String statusMsg = status + (currentTurn != null ? " - " + currentTurn : "");
        displayStatus.put("CASHIER_" + cashierId, statusMsg);
        logger.debug("Estado cajero {}: {}", cashierId, statusMsg);
    }
    
    private void clearTurnFromDisplay(String turnNumber) {
        displayStatus.entrySet().removeIf(entry -> 
            entry.getValue().contains(turnNumber));
        logger.debug("Turno {} removido de pantallas", turnNumber);
    }
    
    private void showTemporaryMessage(String message) {
        logger.debug("Mensaje temporal: {}", message);
        // En producción, esto se removería después de unos segundos
    }
    
    private void updateWaitingCounter(String serviceType) {
        logger.debug("Contador de espera actualizado para {}", serviceType);
    }
    
    private void updateServiceStatus(String serviceType, String status) {
        displayStatus.put("SERVICE_" + serviceType, status);
        logger.debug("Estado servicio {}: {}", serviceType, status);
    }
    
    // Método para obtener el estado actual (útil para APIs)
    public Map<String, String> getCurrentDisplayStatus() {
        return new ConcurrentHashMap<>(displayStatus);
    }
}