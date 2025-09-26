package com.banco.clientes.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tarjetas")
public class Card {
	
	@Id
	private String id;
	
	//propiedades de tarjeta
	private String clientId;
	private String cardNumber;
	private String type;
	private LocalDate issueDate;
	private LocalDate expirationDate;
	private Double creditLimit;
	private String status;
	
	//constructores
	public Card() {}

	public Card(String id, String clientId, String cardNumber, String type, LocalDate issueDate, LocalDate expirDate,
			Double creditLimit, String status) {
		this.id = id;
		this.clientId = clientId;
		this.cardNumber = cardNumber;
		this.type = type;
		this.issueDate = issueDate;
		this.expirationDate = expirDate;
		this.creditLimit = creditLimit;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirDate) {
		this.expirationDate = expirDate;
	}

	public Double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(Double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	
	
	
	

	
	
	

}
