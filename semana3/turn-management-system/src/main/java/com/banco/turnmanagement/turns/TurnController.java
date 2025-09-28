package com.banco.turnmanagement.turns;

import com.banco.turnmanagement.customers.Customer;
import com.banco.turnmanagement.customers.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/turns")
public class TurnController {
    
    @Autowired
    private TurnService turnService;
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<TurnResponse> createTurn(@RequestBody CreateTurnRequest request) {
        try {
            // Obtener o crear cliente
            Customer customer = customerService.getOrCreateCustomer(
                request.getCustomerDocument(), 
                request.getCustomerName()
            );
            
            // Crear turno
            Turn turn = turnService.createTurn(customer.getId(), request.getServiceType());
            
            TurnResponse response = new TurnResponse(
                turn.getId(),
                turn.getTurnNumber(),
                customer.getFullName(),
                turn.getServiceType().toString(),
                turn.getStatus().toString(),
                turn.getCreatedAt().toString()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/queue")
    public ResponseEntity<List<Turn>> getCurrentQueue() {
        List<Turn> waitingTurns = turnService.getWaitingTurns();
        return ResponseEntity.ok(waitingTurns);
    }
    
    @PutMapping("/{id}/call")
    public ResponseEntity<Turn> callTurn(@PathVariable Long id, @RequestBody CallTurnRequest request) {
        Optional<Turn> calledTurn = turnService.callNextTurn(request.getCashierId(), request.getCashierName());
        return calledTurn.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<Turn> completeTurn(@PathVariable Long id) {
        try {
            Turn completedTurn = turnService.completeTurn(id);
            return ResponseEntity.ok(completedTurn);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Clases internas para requests y responses
    public static class CreateTurnRequest {
        private String customerDocument;
        private String customerName;
        private Turn.ServiceType serviceType;
        
        // Constructors
        public CreateTurnRequest() {}
        
        // Getters y Setters
        public String getCustomerDocument() { return customerDocument; }
        public void setCustomerDocument(String customerDocument) { this.customerDocument = customerDocument; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public Turn.ServiceType getServiceType() { return serviceType; }
        public void setServiceType(Turn.ServiceType serviceType) { this.serviceType = serviceType; }
    }
    
    public static class CallTurnRequest {
        private Long cashierId;
        private String cashierName;
        
        // Constructors
        public CallTurnRequest() {}
        
        // Getters y Setters
        public Long getCashierId() { return cashierId; }
        public void setCashierId(Long cashierId) { this.cashierId = cashierId; }
        
        public String getCashierName() { return cashierName; }
        public void setCashierName(String cashierName) { this.cashierName = cashierName; }
    }
    
    public static class TurnResponse {
        private Long id;
        private String turnNumber;
        private String customerName;
        private String serviceType;
        private String status;
        private String createdAt;
        
        public TurnResponse(Long id, String turnNumber, String customerName, 
                           String serviceType, String status, String createdAt) {
            this.id = id;
            this.turnNumber = turnNumber;
            this.customerName = customerName;
            this.serviceType = serviceType;
            this.status = status;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getTurnNumber() { return turnNumber; }
        public String getCustomerName() { return customerName; }
        public String getServiceType() { return serviceType; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
    }
}
