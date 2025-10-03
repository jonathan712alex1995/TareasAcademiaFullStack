package com.banco.turnmanagement.customers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private CustomerService customerService;
    
    private Customer testCustomer;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "12345678", "Juan Pérez");
    }
    
    @Test
    void testCrearCustomer_Success() {
        // Arrange
        when(customerRepository.existsByDocument("12345678")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        
        // Act
        Customer resultado = customerService.createCustomer("12345678", "Juan Pérez");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("12345678", resultado.getDocument());
        assertEquals("Juan Pérez", resultado.getFullname());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testCrearCustomer_DocumentoDuplicado() {
        // Arrange
        when(customerRepository.existsByDocument("12345678")).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer("12345678", "Juan Pérez");
        });
        
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void testFindByDocument_Found() {
        // Arrange
        when(customerRepository.findByDocument("12345678")).thenReturn(Optional.of(testCustomer));
        
        // Act
        Optional<Customer> resultado = customerService.findByDocument("12345678");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan Pérez", resultado.get().getFullname());
    }
    
    @Test
    void testFindByDocument_NotFound() {
        // Arrange
        when(customerRepository.findByDocument("99999999")).thenReturn(Optional.empty());
        
        // Act
        Optional<Customer> resultado = customerService.findByDocument("99999999");
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void testGetOrCreateCustomer_CustomerExiste() {
        // Arrange
        when(customerRepository.findByDocument("12345678")).thenReturn(Optional.of(testCustomer));
        
        // Act
        Customer resultado = customerService.getOrCreateCustomer("12345678", "Juan Pérez");
        
        // Assert
        assertNotNull(resultado);
        assertEquals(testCustomer.getId(), resultado.getId());
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void testGetOrCreateCustomer_CustomerNoExiste() {
        // Arrange
        when(customerRepository.findByDocument("99999999")).thenReturn(Optional.empty());
        when(customerRepository.existsByDocument("99999999")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        
        // Act
        Customer resultado = customerService.getOrCreateCustomer("99999999", "Nuevo Cliente");
        
        // Assert
        assertNotNull(resultado);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}