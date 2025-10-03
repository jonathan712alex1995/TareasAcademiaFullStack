package com.banco.turnmanagement.customers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CreateCustomerRequest request) {
        try {
            Customer customer = customerService.createCustomer(request.getDocument(), request.getFullName());
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{document}")
    public ResponseEntity<Customer> getCustomerByDocument(@PathVariable String document) {
        Optional<Customer> customer = customerService.findByDocument(document);
        return customer.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // Clase interna para el request
    public static class CreateCustomerRequest {
        private String document;
        private String fullName;
        
        // Constructors
        public CreateCustomerRequest() {}
        
        public CreateCustomerRequest(String document, String fullName) {
            this.document = document;
            this.fullName = fullName;
        }
        
        // Getters y Setters
        public String getDocument() { return document; }
        public void setDocument(String document) { this.document = document; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }
}
