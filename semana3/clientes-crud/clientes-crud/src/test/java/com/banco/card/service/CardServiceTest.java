package com.banco.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banco.clientes.model.Card;
import com.banco.clientes.model.Client;
import com.banco.clientes.repository.CardRepository;
import com.banco.clientes.service.CardService;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
	
	@Mock
	private CardRepository cardRepository;
	@InjectMocks
	private CardService cardService;
	
	private Card testCard;
	
	@BeforeEach
	void initCard() {
		testCard = new Card();
		testCard.setId("112233");
		testCard.setCardNumber("1111-2222-3333-4444");
		testCard.setClientId("123");
		testCard.setCreditLimit(5000.0);
		testCard.setExpirationDate(LocalDate.of(2026, 1, 1));
		testCard.setIssueDate(LocalDate.of(2025, 1, 1));
		testCard.setStatus("activa");
		testCard.setType("credito");
	}
	
	@Test
	void testAddNewCard() {
		when(cardRepository.save(testCard)).thenReturn(testCard);
		
		Card result = cardService.addNewCard(testCard);
		assertNotNull(result);
		assertEquals("112233", result.getId());
		assertEquals("1111-2222-3333-4444", result.getCardNumber());
		assertEquals(5000.0, result.getCreditLimit());
		assertEquals("credito", result.getType());
		verify(cardRepository, times(1)).save(testCard);
	}
	
	@Test
	void testFindCardById() {
		String id = "112233";
		when(cardRepository.findById(id)).thenReturn(Optional.of(testCard));
		
		Optional<Card> result= cardService.findCardById(id);
		assertTrue(result.isPresent());
		assertEquals("1111-2222-3333-4444", result.get().getCardNumber());
		verify(cardRepository, times(1)).findById(id);
	}
	
	@Test
	void testShowAllCards() {
		Card card2 = new Card();
		card2.setId("445566");
		card2.setCardNumber("5555-6666-7777-8888");
		card2.setClientId("456");
		card2.setCreditLimit(5000.0);
		card2.setExpirationDate(LocalDate.of(2026, 1, 1));
		card2.setIssueDate(LocalDate.of(2025, 1, 1));
		card2.setStatus("activa");
		card2.setType("credito");
		
		List<Card> cardsList = Arrays.asList(testCard, card2);
		when(cardRepository.findAll()).thenReturn(cardsList);
		List<Card> result = cardService.showAllCards();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("112233", result.get(0).getId());
		assertEquals("445566", result.get(1).getId());
		assertEquals("123", result.get(0).getClientId());
		assertEquals("456", result.get(1).getClientId());
		
		verify(cardRepository , times(1)).findAll();
	
	}
	
	@Test
	void testFindCardByClientId() {
		String clientId = "123";
		
		Card card2 = new Card();
		card2.setId("445566");
		card2.setCardNumber("5555-6666-7777-8888");
		card2.setClientId("123");
		card2.setCreditLimit(null);
		card2.setExpirationDate(LocalDate.of(2026, 1, 1));
		card2.setIssueDate(LocalDate.of(2025, 1, 1));
		card2.setStatus("activa");
		card2.setType("debito");
		
		List<Card> clientCards = Arrays.asList(testCard , card2);
		
		when(cardRepository.findByClientId(clientId)).thenReturn(clientCards);
		
		List<Card> result = cardService.findCardsByClientId(clientId);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("123", result.get(0).getClientId());
		assertEquals("123", result.get(1).getClientId());
		assertEquals("1111-3222-3333-4444", result.get(0).getCardNumber());
		assertEquals("5555-6666-7777-8888", result.get(1).getCardNumber());
		
		verify(cardRepository , times(1)).findByClientId(clientId);
		
		
	}
	
}
