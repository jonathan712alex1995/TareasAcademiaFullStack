package com.banco.turnmanagement.turns;

import com.banco.turnmanagement.customers.Customer;
import com.banco.turnmanagement.customers.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TurnController.class)
class TurnControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TurnService turnService;
    
    @MockBean
    private CustomerService customerService;
    
    private Turn testTurn;
    private Customer testCustomer;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "12345678", "Juan Pérez");
        testTurn = new Turn(
            1L,
            1L,
            Turn.ServiceType.CAJA,
            "C001",
            Turn.TurnStatus.ESPERA,
            LocalDateTime.now(),
            null
        );
    }
    
    @Test
    void testCreateTurn_Success() throws Exception {
        // Arrange
        when(customerService.getOrCreateCustomer(anyString(), anyString())).thenReturn(testCustomer);
        when(turnService.createTurn(anyLong(), any(Turn.ServiceType.class))).thenReturn(testTurn);
        
        TurnController.CreateTurnRequest request = new TurnController.CreateTurnRequest();
        request.setCustomerDocument("12345678");
        request.setCustomerName("Juan Pérez");
        request.setServiceType(Turn.ServiceType.CAJA);
        
        // Act & Assert
        mockMvc.perform(post("/api/turns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turnNumber").value("C001"))
                .andExpect(jsonPath("$.customerName").value("Juan Pérez"))
                .andExpect(jsonPath("$.serviceType").value("CAJA"));
        
        verify(customerService, times(1)).getOrCreateCustomer("12345678", "Juan Pérez");
        verify(turnService, times(1)).createTurn(1L, Turn.ServiceType.CAJA);
    }
    
    @Test
    void testCreateTurn_BadRequest() throws Exception {
        // Arrange
        when(customerService.getOrCreateCustomer(anyString(), anyString()))
            .thenThrow(new RuntimeException("Error"));
        
        TurnController.CreateTurnRequest request = new TurnController.CreateTurnRequest();
        request.setCustomerDocument("12345678");
        request.setCustomerName("Juan Pérez");
        request.setServiceType(Turn.ServiceType.CAJA);
        
        // Act & Assert
        mockMvc.perform(post("/api/turns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetCurrentQueue_Success() throws Exception {
        // Arrange
        Turn turn1 = new Turn(1L, 1L, Turn.ServiceType.CAJA, "C001", Turn.TurnStatus.ESPERA, LocalDateTime.now(), null);
        Turn turn2 = new Turn(2L, 2L, Turn.ServiceType.CAJA, "C002", Turn.TurnStatus.ESPERA, LocalDateTime.now(), null);
        List<Turn> waitingTurns = Arrays.asList(turn1, turn2);
        
        when(turnService.getWaitingTurns()).thenReturn(waitingTurns);
        
        // Act & Assert
        mockMvc.perform(get("/api/turns/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].turnNumber").value("C001"))
                .andExpect(jsonPath("$[1].turnNumber").value("C002"));
        
        verify(turnService, times(1)).getWaitingTurns();
    }
    
    @Test
    void testCallTurn_Success() throws Exception {
        // Arrange
        Turn calledTurn = new Turn(
            1L, 1L, Turn.ServiceType.CAJA, "C001", 
            Turn.TurnStatus.LLAMADO, LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(turnService.callNextTurn(anyLong(), anyString())).thenReturn(Optional.of(calledTurn));
        
        TurnController.CallTurnRequest request = new TurnController.CallTurnRequest();
        request.setCashierId(1L);
        request.setCashierName("Juan Cajero");
        
        // Act & Assert
        mockMvc.perform(put("/api/turns/1/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LLAMADO"));
        
        verify(turnService, times(1)).callNextTurn(1L, "Juan Cajero");
    }
    
    @Test
    void testCallTurn_NotFound() throws Exception {
        // Arrange
        when(turnService.callNextTurn(anyLong(), anyString())).thenReturn(Optional.empty());
        
        TurnController.CallTurnRequest request = new TurnController.CallTurnRequest();
        request.setCashierId(1L);
        request.setCashierName("Juan Cajero");
        
        // Act & Assert
        mockMvc.perform(put("/api/turns/1/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testCompleteTurn_Success() throws Exception {
        // Arrange
        Turn completedTurn = new Turn(
            1L, 1L, Turn.ServiceType.CAJA, "C001", 
            Turn.TurnStatus.COMPLETADO, LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(turnService.completeTurn(1L)).thenReturn(completedTurn);
        
        // Act & Assert
        mockMvc.perform(put("/api/turns/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETADO"));
        
        verify(turnService, times(1)).completeTurn(1L);
    }
    
    @Test
    void testCompleteTurn_NotFound() throws Exception {
        // Arrange
        when(turnService.completeTurn(999L))
            .thenThrow(new IllegalArgumentException("Turn not found"));
        
        // Act & Assert
        mockMvc.perform(put("/api/turns/999/complete"))
                .andExpect(status().isNotFound());
    }
}
