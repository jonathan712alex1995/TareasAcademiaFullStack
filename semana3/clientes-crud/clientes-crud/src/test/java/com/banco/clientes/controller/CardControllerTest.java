package com.banco.clientes.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.banco.clientes.model.Card;
import com.banco.clientes.service.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
public class CardControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CardService cardService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	Card testCard, testCard2, testCard3;
	List<Card> cardsList;
	
	@BeforeEach
	void initCards() {
		testCard = new Card("112233","123","1111-2222-3333-4444","credito",LocalDate.of(2025, 1, 1),
                			LocalDate.of(2026, 1, 1),5000.0,"activa");
		testCard2 = new Card("445566","123","5555-6666-7777-8888","debito",LocalDate.of(2025, 1, 1),
				  			LocalDate.of(2026, 1, 1),null,"activa" );
		testCard3 = new Card("778899","123","9999-1010-1111-1212","debito",LocalDate.of(2025, 1, 1),
                			LocalDate.of(2026, 1, 1),null,"activa" );
		
	}
	
	@Test
	void testGetAllCards() throws Exception {
		cardsList = Arrays.asList(testCard, testCard2, testCard3);
		
		when(cardService.showAllCards()).thenReturn(cardsList);
		
		mockMvc.perform(get("/api/cards"))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$" , hasSize(3)))
		.andExpect(jsonPath("$[0].id" , is("112233")))
		.andExpect(jsonPath("$[1].id" , is("445566")))
		.andExpect(jsonPath("$[2].id" , is("778899")));
		
	}
	
	@Test
	void testGetCardById() throws Exception {
		when(cardService.findCardById("112233")).thenReturn(Optional.of(testCard));
		
		mockMvc.perform(get("/api/cards/{id}" , "112233"))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$.id" , is("112233")))
		.andExpect(jsonPath("$.cardNumber" , is("1111-2222-3333-4444")));
	}
	
	@Test
	void testCreateCard() throws JsonProcessingException, Exception {
		when(cardService.addNewCard(any(Card.class))).thenReturn(testCard);
		
		mockMvc.perform(post("/api/cards")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCard)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.id" , is("112233")))
				.andExpect(jsonPath("$.cardNumber" , is("1111-2222-3333-4444")));
				
	}
	
	@Test
	void testUpdatedCard() throws JsonProcessingException, Exception {
		Card updatedCard = new Card ("112233","123","0000-0000-0000-0000","credito",LocalDate.of(2025, 1, 1),
    			LocalDate.of(2026, 1, 1),5000.0,"activa");
		
		when(cardService.updateCard(eq("112233"), any(Card.class))).thenReturn(updatedCard);
		
		mockMvc.perform(put("/api/cards/{id}" , "112233")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCard)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.id" , is("112233")))
				.andExpect(jsonPath("$.cardNumber" , is("0000-0000-0000-0000")));
		
	}
	
	@Test
	void testDeleteCard() throws Exception {
		when(cardService.deleteCardById(eq("112233"))).thenReturn(true);
		
		mockMvc.perform(delete("/api/cards/{id}" , "112233"))
		.andExpect(status().isNoContent());	
	}
	
	
	
	

}
