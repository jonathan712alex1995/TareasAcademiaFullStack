package com.banco.turnmanagement.turns;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "turns")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
