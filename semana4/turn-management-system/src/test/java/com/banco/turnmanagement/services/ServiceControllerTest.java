package com.banco.turnmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceController.class)
class ServiceControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CashierService cashierService;
    
    private Cashier testCashier;
    
    @BeforeEach
    void setUp() {
        testCashier = new Cashier(
            1L,
            "Juan Pérez",
            Cashier.ServiceType.CAJA,
            Cashier.CashierStatus.DISPONIBLE
        );
    }
    
    @Test
    void testCreateCashier_Success() throws Exception {
        // Arrange
        when(cashierService.createCashier(anyString(), any(Cashier.ServiceType.class)))
            .thenReturn(testCashier);
        
        ServiceController.CreateCashierRequest request = 
            new ServiceController.CreateCashierRequest("Juan Pérez", Cashier.ServiceType.CAJA);
        
        // Act & Assert
        mockMvc.perform(post("/api/services/cashiers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.serviceType").value("CAJA"))
                .andExpect(jsonPath("$.cashierStatus").value("DISPONIBLE"));
        
        verify(cashierService, times(1)).createCashier("Juan Pérez", Cashier.ServiceType.CAJA);
    }
    
    @Test
    void testCreateCashier_BadRequest() throws Exception {
        // Arrange
        when(cashierService.createCashier(anyString(), any(Cashier.ServiceType.class)))
            .thenThrow(new RuntimeException("Error"));
        
        ServiceController.CreateCashierRequest request = 
            new ServiceController.CreateCashierRequest("Juan Pérez", Cashier.ServiceType.CAJA);
        
        // Act & Assert
        mockMvc.perform(post("/api/services/cashiers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAllCashiers_Success() throws Exception {
        // Arrange
        Cashier cashier1 = new Cashier(1L, "Juan", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE);
        Cashier cashier2 = new Cashier(2L, "María", Cashier.ServiceType.EJECUTIVO, Cashier.CashierStatus.DESCANSO);
        List<Cashier> cashiers = Arrays.asList(cashier1, cashier2);
        
        when(cashierService.getAllCashiers()).thenReturn(cashiers);
        
        // Act & Assert
        mockMvc.perform(get("/api/services/cashiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[1].name").value("María"));
        
        verify(cashierService, times(1)).getAllCashiers();
    }
    
    @Test
    void testGetAvailableCashiers_Success() throws Exception {
        // Arrange
        Cashier cashier1 = new Cashier(1L, "Juan", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE);
        List<Cashier> availableCashiers = Arrays.asList(cashier1);
        
        when(cashierService.getAvailableCashiers()).thenReturn(availableCashiers);
        
        // Act & Assert
        mockMvc.perform(get("/api/services/cashiers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cashierStatus").value("DISPONIBLE"));
        
        verify(cashierService, times(1)).getAvailableCashiers();
    }
    
    @Test
    void testUpdateCashierStatus_Success() throws Exception {
        // Arrange
        Cashier updatedCashier = new Cashier(1L, "Juan Pérez", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DESCANSO);
        when(cashierService.updateCashierStatus(anyLong(), any(Cashier.CashierStatus.class)))
            .thenReturn(updatedCashier);
        
        ServiceController.UpdateStatusRequest request = 
            new ServiceController.UpdateStatusRequest(Cashier.CashierStatus.DESCANSO);
        
        // Act & Assert
        mockMvc.perform(put("/api/services/cashiers/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cashierStatus").value("DESCANSO"));
        
        verify(cashierService, times(1)).updateCashierStatus(1L, Cashier.CashierStatus.DESCANSO);
    }
    
    @Test
    void testUpdateCashierStatus_NotFound() throws Exception {
        // Arrange
        when(cashierService.updateCashierStatus(anyLong(), any(Cashier.CashierStatus.class)))
            .thenThrow(new IllegalArgumentException("Cashier not found"));
        
        ServiceController.UpdateStatusRequest request = 
            new ServiceController.UpdateStatusRequest(Cashier.CashierStatus.DESCANSO);
        
        // Act & Assert
        mockMvc.perform(put("/api/services/cashiers/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetCashierById_Found() throws Exception {
        // Arrange
        when(cashierService.findById(1L)).thenReturn(Optional.of(testCashier));
        
        // Act & Assert
        mockMvc.perform(get("/api/services/cashiers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.id").value(1));
        
        verify(cashierService, times(1)).findById(1L);
    }
    
    @Test
    void testGetCashierById_NotFound() throws Exception {
        // Arrange
        when(cashierService.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/services/cashiers/999"))
                .andExpect(status().isNotFound());
        
        verify(cashierService, times(1)).findById(999L);
    }
}
