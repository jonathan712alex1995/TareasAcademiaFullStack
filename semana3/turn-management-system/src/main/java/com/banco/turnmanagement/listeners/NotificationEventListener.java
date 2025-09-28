package com.banco.turnmanagement.listeners;

import com.banco.turnmanagement.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);
    
    @EventListener
    public void handleTurnCreated(TurnCreated event) {
        logger.info("NOTIFICACIÓN: Turno {} creado para cliente ID {}", 
            event.turnNumber(), event.customerId());
        
        // Aquí iría la lógica para enviar SMS/Email al cliente
        sendSMSToCustomer(event.customerId(), event.turnNumber());
    }
    
    @EventListener
    public void handleTurnCalled(TurnCalled event) {
        logger.info("NOTIFICACIÓN: Turno {} llamado por cajero {}", 
            event.turnNumber(), event.cashierName());
        
        // Aquí iría la lógica para notificar al cliente que vaya
        notifyCustomerTurnReady(event.turnNumber());
    }
    
    @EventListener
    public void handleTurnCompleted(TurnCompleted event) {
        logger.info("NOTIFICACIÓN: Turno {} completado exitosamente", 
            event.turnNumber());
        
        // Aquí iría la lógica para enviar confirmación o encuesta
        sendCompletionSurvey(event.turnId());
    }
    
    @EventListener
    public void handleCashierAvailable(CashierAvailable event) {
        logger.info("NOTIFICACIÓN: Cajero {} ({}) ahora disponible para {}", 
            event.cashierName(), event.cashierId(), event.serviceType());
        
        // Aquí iría la lógica para notificar al sistema de gestión
        notifyManagementSystem(event);
    }
    
    // Métodos simulados (en producción conectarían con servicios reales)
    private void sendSMSToCustomer(Long customerId, String turnNumber) {
        logger.debug("SMS enviado a cliente {}: Su turno es {}", customerId, turnNumber);
    }
    
    private void notifyCustomerTurnReady(String turnNumber) {
        logger.debug("Anuncio: Cliente con turno {} diríjase al mostrador", turnNumber);
    }
    
    private void sendCompletionSurvey(Long turnId) {
        logger.debug("Encuesta enviada para turno ID {}", turnId);
    }
    
    private void notifyManagementSystem(CashierAvailable event) {
        logger.debug("Sistema de gestión notificado: cajero disponible");
    }
}
