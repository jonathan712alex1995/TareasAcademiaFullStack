package com.banco.turnmanagement.customers;

import jakarta.persistence.*;


@Entity
@Table(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false, unique=true)
	private String document;
	
	@Column(nullable=false)
	private String fullname;

	
	
	
	public Customer() {
		
	}




	public Customer(long id, String document, String fullname) {
		super();
		this.id = id;
		this.document = document;
		this.fullname = fullname;
	}




	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}




	public String getDocument() {
		return document;
	}




	public void setDocument(String document) {
		this.document = document;
	}




	public String getFullname() {
		return fullname;
	}




	public void setFullname(String fullname) {
		this.fullname = fullname;
	}




	




	
	
	
	
	}
