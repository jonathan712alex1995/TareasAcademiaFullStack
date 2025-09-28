package com.banco.turnmanagement.turns;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "turns")
public class Turn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Long customerId;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType;
	
	@Column(nullable = false , unique=true)
	private String turnNumber;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private TurnStatus status;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	private LocalDateTime calledAt;
	
	public enum ServiceType{
		CAJA, EJECUTIVO
	}
	
	public enum TurnStatus{
		ESPERA, LLAMADO, ATENDIENDO, COMPLETADO, CANCELADO
	}
	
	

	public Turn() {
		
	}



	public Turn(Long id, Long customerId, ServiceType serviceType, String turnNumber, TurnStatus status,
			LocalDateTime createdAt, LocalDateTime calledAt) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.serviceType = serviceType;
		this.turnNumber = turnNumber;
		this.status = status;
		this.createdAt = createdAt;
		this.calledAt = calledAt;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getCustomerId() {
		return customerId;
	}



	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}



	public ServiceType getServiceType() {
		return serviceType;
	}



	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}



	public String getTurnNumber() {
		return turnNumber;
	}



	public void setTurnNumber(String turnNumber) {
		this.turnNumber = turnNumber;
	}



	public TurnStatus getStatus() {
		return status;
	}



	public void setStatus(TurnStatus status) {
		this.status = status;
	}



	public LocalDateTime getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public LocalDateTime getCalledAt() {
		return calledAt;
	}



	public void setCalledAt(LocalDateTime calledAt) {
		this.calledAt = calledAt;
	}
	
	
	
	
}
