package com.banco.turnmanagement.services;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cashiers")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
	private CashierStatus cashierStatus;
	
	public enum ServiceType{
		CAJA, EJECUTIVO
	}
	
	public enum CashierStatus{
		DISPONIBLE, OCUPADO, DESCANSO
	}
}
