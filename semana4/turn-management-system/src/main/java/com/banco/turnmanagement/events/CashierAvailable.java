package com.banco.turnmanagement.events;

public record CashierAvailable(
	    Long cashierId,
	    String cashierName,
	    String serviceType
	) {}
