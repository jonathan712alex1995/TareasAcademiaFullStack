package com.banco.turnmanagement.customers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CustomerService customerService;
    
    private Customer testCustomer;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "12345678", "Juan Pérez");
    }
    
    @Test
    void testCreateCustomer_Success() throws Exception {
        // Arrange
        when(customerService.createCustomer(anyString(), anyString())).thenReturn(testCustomer);
        
        CustomerController.CreateCustomerRequest request = 
            new CustomerController.CreateCustomerRequest("12345678", "Juan Pérez");
        
        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("12345678"))
                .andExpect(jsonPath("$.fullname").value("Juan Pérez"));
        
        verify(customerService, times(1)).createCustomer("12345678", "Juan Pérez");
    }
    
    @Test
    void testCreateCustomer_BadRequest() throws Exception {
        // Arrange
        when(customerService.createCustomer(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Email duplicado"));
        
        CustomerController.CreateCustomerRequest request = 
            new CustomerController.CreateCustomerRequest("12345678", "Juan Pérez");
        
        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetCustomerByDocument_Found() throws Exception {
        // Arrange
        when(customerService.findByDocument("12345678")).thenReturn(Optional.of(testCustomer));
        
        // Act & Assert
        mockMvc.perform(get("/api/customers/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("12345678"))
                .andExpect(jsonPath("$.fullname").value("Juan Pérez"));
        
        verify(customerService, times(1)).findByDocument("12345678");
    }
    
    @Test
    void testGetCustomerByDocument_NotFound() throws Exception {
        // Arrange
        when(customerService.findByDocument("99999999")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/customers/99999999"))
                .andExpect(status().isNotFound());
        
        verify(customerService, times(1)).findByDocument("99999999");
    }
}
