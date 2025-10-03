package com.banco.turnmanagement.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CashierRepository extends JpaRepository<Cashier, Long> {
    
    List<Cashier> findByServiceTypeAndStatus(Cashier.ServiceType serviceType, Cashier.CashierStatus status);
    
    List<Cashier> findByStatus(Cashier.CashierStatus status);
}
