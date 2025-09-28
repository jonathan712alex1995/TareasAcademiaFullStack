package com.banco.turnmanagement.services;

import com.banco.turnmanagement.events.CashierAvailable;
import com.banco.turnmanagement.services.Cashier.CashierStatus;
import static com.banco.turnmanagement.services.Cashier.CashierStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CashierService {
    
    @Autowired
    private CashierRepository cashierRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public Cashier createCashier(String name, Cashier.ServiceType serviceType) {
        Cashier cashier = new Cashier(
            null,
            name,
            serviceType,
            Cashier.CashierStatus.DISPONIBLE
        );
        
        return cashierRepository.save(cashier);
    }
    
    public List<Cashier> getAvailableCashiers() {
        return cashierRepository.findByStatus(Cashier.CashierStatus.DISPONIBLE);
    }
    
    public List<Cashier> getAvailableCashiersByService(Cashier.ServiceType serviceType) {
        return cashierRepository.findByServiceTypeAndStatus(serviceType, Cashier.CashierStatus.DISPONIBLE);
    }
    
    public Cashier updateCashierStatus(Long cashierId, CashierStatus newStatus) {
        Cashier cashier = cashierRepository.findById(cashierId)
            .orElseThrow(() -> new IllegalArgumentException("Cashier not found"));
        
        Cashier.CashierStatus oldStatus = cashier.getCashierStatus();
        cashier.setCashierStatus(newStatus);
        
        Cashier savedCashier = cashierRepository.save(cashier);
        
        // Si el cajero se vuelve disponible, publicar evento
        if (newStatus == Cashier.CashierStatus.DISPONIBLE && oldStatus != Cashier.CashierStatus.DISPONIBLE) {
            CashierAvailable event = new CashierAvailable(
                savedCashier.getId(),
                savedCashier.getName(),
                savedCashier.getServiceType().toString()
            );
            eventPublisher.publishEvent(event);
        }
        
        return savedCashier;
    }
    
    public List<Cashier> getAllCashiers() {
        return cashierRepository.findAll();
    }
    
    public Optional<Cashier> findById(Long id) {
        return cashierRepository.findById(id);
    }
}