package com.banco.turnmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    
    @Autowired
    private CashierService cashierService;
    
    @PostMapping("/cashiers")
    public ResponseEntity<Cashier> createCashier(@RequestBody CreateCashierRequest request) {
        try {
            Cashier cashier = cashierService.createCashier(request.getName(), request.getServiceType());
            return ResponseEntity.ok(cashier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/cashiers")
    public ResponseEntity<List<Cashier>> getAllCashiers() {
        List<Cashier> cashiers = cashierService.getAllCashiers();
        return ResponseEntity.ok(cashiers);
    }
    
    @GetMapping("/cashiers/available")
    public ResponseEntity<List<Cashier>> getAvailableCashiers() {
        List<Cashier> availableCashiers = cashierService.getAvailableCashiers();
        return ResponseEntity.ok(availableCashiers);
    }
    
    @PutMapping("/cashiers/{id}/status")
    public ResponseEntity<Cashier> updateCashierStatus(
            @PathVariable Long id, 
            @RequestBody UpdateStatusRequest request) {
        try {
            Cashier updatedCashier = cashierService.updateCashierStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedCashier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/cashiers/{id}")
    public ResponseEntity<Cashier> getCashierById(@PathVariable Long id) {
        return cashierService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Clases internas para requests
    public static class CreateCashierRequest {
        private String name;
        private Cashier.ServiceType serviceType;
        
        // Constructors
        public CreateCashierRequest() {}
        
        public CreateCashierRequest(String name, Cashier.ServiceType serviceType) {
            this.name = name;
            this.serviceType = serviceType;
        }
        
        // Getters y Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Cashier.ServiceType getServiceType() { return serviceType; }
        public void setServiceType(Cashier.ServiceType serviceType) { this.serviceType = serviceType; }
    }
    
    public static class UpdateStatusRequest {
        private Cashier.CashierStatus status;
        
        // Constructors
        public UpdateStatusRequest() {}
        
        public UpdateStatusRequest(Cashier.CashierStatus status) {
            this.status = status;
        }
        
        // Getters y Setters
        public Cashier.CashierStatus getStatus() { return status; }
        public void setStatus(Cashier.CashierStatus status) { this.status = status; }
    }
}
