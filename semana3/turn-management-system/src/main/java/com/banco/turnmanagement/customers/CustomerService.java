package com.banco.turnmanagement.customers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public Customer createCustomer(String document, String fullName) {
        if (customerRepository.existsByDocument(document)) {
            throw new IllegalArgumentException("Customer with document " + document + " already exists");
        }
        
        Customer customer = new Customer();
        customer.setDocument(document);
        customer.setFullname(fullName);
        
        return customerRepository.save(customer);
    }
    
    public Optional<Customer> findByDocument(String document) {
        return customerRepository.findByDocument(document);
    }
    
    public Customer getOrCreateCustomer(String document, String fullName) {
        Optional<Customer> existing = findByDocument(document);
        if (existing.isPresent()) {
            return existing.get();
        }
        return createCustomer(document, fullName);
    }
}
