package com.banco.turnmanagement.services;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cashiers")
public class Cashier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CashierStatus status;
	
	public enum ServiceType{
		CAJA, EJECUTIVO
	}
	
	public enum CashierStatus{
		DISPONIBLE, OCUPADO, DESCANSO
	}

	public Cashier() {
		
	}

	public Cashier(Long id, String name, ServiceType serviceType, CashierStatus cashierStatus) {
		this.id = id;
		this.name = name;
		this.serviceType = serviceType;
		this.status = cashierStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public CashierStatus getCashierStatus() {
		return status;
	}

	public void setCashierStatus(CashierStatus cashierStatus) {
		this.status = cashierStatus;
	}
	
	
	
}
