package com.banco.turnmanagement.events;

import java.time.LocalDateTime;

public record TurnCalled(

		Long turnId,
	    String turnNumber,
	    Long cashierId,
	    String cashierName,
	    LocalDateTime calledAt
		
) {}
