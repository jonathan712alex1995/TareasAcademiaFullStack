package com.banco.turnmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashierServiceTest {
    
    @Mock
    private CashierRepository cashierRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
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
    void testCreateCashier_Success() {
        // Arrange
        when(cashierRepository.save(any(Cashier.class))).thenReturn(testCashier);
        
        // Act
        Cashier resultado = cashierService.createCashier("Juan Pérez", Cashier.ServiceType.CAJA);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getName());
        assertEquals(Cashier.ServiceType.CAJA, resultado.getServiceType());
        assertEquals(Cashier.CashierStatus.DISPONIBLE, resultado.getCashierStatus());
        verify(cashierRepository, times(1)).save(any(Cashier.class));
    }
    
    @Test
    void testGetAvailableCashiers() {
        // Arrange
        Cashier cashier1 = new Cashier(1L, "Juan", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE);
        Cashier cashier2 = new Cashier(2L, "María", Cashier.ServiceType.EJECUTIVO, Cashier.CashierStatus.DISPONIBLE);
        
        when(cashierRepository.findByStatus(Cashier.CashierStatus.DISPONIBLE))
            .thenReturn(Arrays.asList(cashier1, cashier2));
        
        // Act
        List<Cashier> resultado = cashierService.getAvailableCashiers();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(cashierRepository, times(1)).findByStatus(Cashier.CashierStatus.DISPONIBLE);
    }
    
    @Test
    void testGetAvailableCashiersByService() {
        // Arrange
        Cashier cashier1 = new Cashier(1L, "Juan", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE);
        
        when(cashierRepository.findByServiceTypeAndStatus(Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE))
            .thenReturn(Arrays.asList(cashier1));
        
        // Act
        List<Cashier> resultado = cashierService.getAvailableCashiersByService(Cashier.ServiceType.CAJA);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Cashier.ServiceType.CAJA, resultado.get(0).getServiceType());
    }
    
    @Test
    void testUpdateCashierStatus_ToBusy() {
        // Arrange
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(testCashier));
        
        Cashier updatedCashier = new Cashier(1L, "Juan Pérez", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DESCANSO);
        when(cashierRepository.save(any(Cashier.class))).thenReturn(updatedCashier);
        
        // Act
        Cashier resultado = cashierService.updateCashierStatus(1L, Cashier.CashierStatus.DESCANSO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(Cashier.CashierStatus.DESCANSO, resultado.getCashierStatus());
        verify(cashierRepository, times(1)).save(any(Cashier.class));
        verify(eventPublisher, never()).publishEvent(any());  // No evento porque no se volvió AVAILABLE
    }
    
    @Test
    void testUpdateCashierStatus_ToAvailable_PublishesEvent() {
        // Arrange
        Cashier busyCashier = new Cashier(1L, "Juan Pérez", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DESCANSO);
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(busyCashier));
        
        Cashier availableCashier = new Cashier(1L, "Juan Pérez", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE);
        when(cashierRepository.save(any(Cashier.class))).thenReturn(availableCashier);
        
        // Act
        Cashier resultado = cashierService.updateCashierStatus(1L, Cashier.CashierStatus.DISPONIBLE);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(Cashier.CashierStatus.DISPONIBLE, resultado.getCashierStatus());
        verify(cashierRepository, times(1)).save(any(Cashier.class));
        verify(eventPublisher, times(1)).publishEvent(any()); // ⭐ Evento publicado
    }
    
    @Test
    void testUpdateCashierStatus_CashierNotFound() {
        // Arrange
        when(cashierRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cashierService.updateCashierStatus(999L, Cashier.CashierStatus.DESCANSO);
        });
        
        verify(cashierRepository, never()).save(any(Cashier.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
    
    @Test
    void testGetAllCashiers() {
        // Arrange
        List<Cashier> cashiers = Arrays.asList(
            new Cashier(1L, "Juan", Cashier.ServiceType.CAJA, Cashier.CashierStatus.DISPONIBLE),
            new Cashier(2L, "María", Cashier.ServiceType.EJECUTIVO, Cashier.CashierStatus.DESCANSO)
        );
        when(cashierRepository.findAll()).thenReturn(cashiers);
        
        // Act
        List<Cashier> resultado = cashierService.getAllCashiers();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(cashierRepository, times(1)).findAll();
    }
    
    @Test
    void testFindById_Found() {
        // Arrange
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(testCashier));
        
        // Act
        Optional<Cashier> resultado = cashierService.findById(1L);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan Pérez", resultado.get().getName());
    }
    
    @Test
    void testFindById_NotFound() {
        // Arrange
        when(cashierRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Cashier> resultado = cashierService.findById(999L);
        
        // Assert
        assertFalse(resultado.isPresent());
    }
}
