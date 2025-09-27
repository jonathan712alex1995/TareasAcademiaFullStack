package com.banco.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
	
	private Card testCard,testCard2,testCard3,testCard4;
	
	@BeforeEach
	void initCard() { 
		testCard = new Card("112233","123","1111-2222-3333-4444","credito",LocalDate.of(2025, 1, 1),
				                 LocalDate.of(2026, 1, 1),5000.0,"activa");
		
		testCard2 = new Card("445566","123","5555-6666-7777-8888","debito",LocalDate.of(2025, 1, 1),
								  LocalDate.of(2026, 1, 1),null,"activa" ); 
		testCard3 = new Card("778899","123","9999-1010-1111-1212","debito",LocalDate.of(2025, 1, 1),
				                  LocalDate.of(2026, 1, 1),null,"activa" );

		testCard4 = new Card("101112","123","1313-1414-1515-1616","debito",LocalDate.of(2025, 1, 1),
				                  LocalDate.of(2026, 1, 1),null,"activa" );
		
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
		
		testCard2.setClientId("456");
		
		List<Card> cardsList = Arrays.asList(testCard, testCard2);
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
		
		testCard2.setClientId("123");
		
		List<Card> clientCards = Arrays.asList(testCard , testCard2);
		
		when(cardRepository.findByClientId("123")).thenReturn(clientCards);
		
		List<Card> result = cardService.findCardsByClientId("123");
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("123", result.get(0).getClientId());
		assertEquals("123", result.get(1).getClientId());
		assertEquals("1111-2222-3333-4444", result.get(0).getCardNumber());
		assertEquals("5555-6666-7777-8888", result.get(1).getCardNumber());
		
		verify(cardRepository , times(1)).findByClientId("123");
		
		
	}
	
	@Test
	void findCardsByType() {
		
		
		List<Card> listCards = Arrays.asList(testCard2 , testCard3 , testCard4);
		
		when(cardRepository.findByType("debito")).thenReturn(listCards);
		
		List<Card> result = cardService.findCardsByType("debito");
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("5555-6666-7777-8888", result.get(0).getCardNumber());
		assertEquals("9999-1010-1111-1212", result.get(1).getCardNumber());
		assertEquals("1313-1414-1515-1616", result.get(2).getCardNumber());
		
		verify(cardRepository , times(1)).findByType("debito");
		
		
	}
	
	@Test
	void testFindCardByStatus() {
		testCard2.setStatus("cancelada");
		testCard3.setStatus("cancelada");
		
		List<Card> cardsList = Arrays.asList(testCard2 , testCard3);
		when(cardRepository.findByStatus("cancelada")).thenReturn(cardsList);
		
		List<Card> result = cardService.findCardsByStatus("cancelada");
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("445566", result.get(0).getId());
		assertEquals("778899", result.get(1).getId());
		
		verify(cardRepository , times(1)).findByStatus("cancelada");
		
		
	}
	
	@Test
	void testUpdatedCard() {
		
		Card updatedCard = testCard2;
		updatedCard.setCardNumber("0000-0000-0000-0000");
		
		when(cardRepository.findById("445566")).thenReturn(Optional.of(testCard2));
		when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);
		
		Card result = cardService.updateCard("445566", updatedCard);
		
		assertNotNull(result);
		assertEquals("445566", result.getId());
		assertEquals("0000-0000-0000-0000", result.getCardNumber());
		
		verify(cardRepository, times(1)).findById("445566");
		verify(cardRepository, times(1)).save(updatedCard);
	}
	
	@Test
	void testDeleteCard() {
		
		when(cardRepository.existsById("112233")).thenReturn(true);
		doNothing().when(cardRepository).deleteById("112233");
		
		boolean result = cardService.deleteCardById("112233");
		
		assertTrue(result);
		
		verify(cardRepository , times(1)).existsById("112233");
		verify(cardRepository , times(1)).deleteById("112233");
	}
	
}
