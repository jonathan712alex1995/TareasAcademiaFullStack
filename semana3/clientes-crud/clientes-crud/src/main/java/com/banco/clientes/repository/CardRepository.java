package com.banco.clientes.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.banco.clientes.model.Card;

@Repository
public interface CardRepository extends MongoRepository<Card, String>{

	List<Card> findByClientId(String clientId);
	List<Card> findByType(String type);
	List<Card> findByStatus(String status);
	//List<Card> disableClientCard(String cardId);
	
}
