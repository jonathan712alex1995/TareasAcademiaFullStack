package com.banco.turnmanagement.events;

import java.time.LocalDateTime;

public record TurnCreated(

		Long turnId,
		Long customerId,
		String turnNumber,
		String serviceType,
		LocalDateTime createdAt
		
){}
