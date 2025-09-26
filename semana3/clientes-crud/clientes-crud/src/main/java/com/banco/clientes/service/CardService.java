package com.banco.clientes.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.clientes.model.Card;
import com.banco.clientes.repository.CardRepository;

@Service
public class CardService {

	List<String> validStatusCard = Arrays.asList("activa", "suspendida", "cancelada");
	@Autowired
	private CardRepository cardRepository;
	
	//metodos CRUD
	public Card addNewCard(Card card) {
		return cardRepository.save(card);
	}
	
	public List<Card> showAllCards(){
		return cardRepository.findAll();
	}
	
	public Optional<Card> findCardById(String id){
		return cardRepository.findById(id);
	}
	
	public List<Card> findCardsByClientId(String clientId){
		return cardRepository.findByClientId(clientId);
	}
	
	public List<Card> findCardsByType(String type){
		return cardRepository.findByType(type);
	}
	
	public List<Card> findCardsByStatus(String status){
		return cardRepository.findByStatus(status);
	}
	
	//UPDATE
	public Card updateCard(String id, Card updatedCard) {
		Optional<Card> existCard = cardRepository.findById(id);
		if(existCard.isPresent()) {
			Card card = existCard.get();
			card.setCardNumber(updatedCard.getCardNumber());
			card.setCreditLimit(updatedCard.getCreditLimit());
			card.setExpirationDate(updatedCard.getExpirationDate());
			card.setIssueDate(updatedCard.getIssueDate());
			card.setId(updatedCard.getId());
			card.setStatus(updatedCard.getStatus());
			card.setType(updatedCard.getType());
			return cardRepository.save(card);
		}
		return null;
	}
	
	public boolean disableClientCard(String cardId , String status) {
		if(validStatusCard.contains(status)) {
			Optional<Card> existCard = cardRepository.findById(cardId);
			if(existCard.isPresent()) {
				Card card = existCard.get();
				card.setStatus(status);
				cardRepository.save(card);
				return true;
			}
		}
		
		return false;
	}
	
	//DELETE
	public boolean deleteCardById(String id) {
		if(cardRepository.existsById(id)) {
			cardRepository.deleteById(id);
			return true;
		}
		return false;
	}
	
	
}
