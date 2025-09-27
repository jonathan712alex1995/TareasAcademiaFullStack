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

import com.banco.clientes.model.Client;
import com.banco.clientes.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Client testClient, testClient2;
    private List<Client> clientList;
    
    @BeforeEach
    void initClient() {
    	testClient = new Client("456","Ana Martínez",'F',"Mexicana",LocalDate.of(1990, 5, 14),"Av. Reforma 123",
                         "Ciudad de México","01000","México","555-123-4567","044-555-987-6543",
                			"ana.martinez@example.com");
    	testClient2 = new Client("123","Juan Pérez",'M',"Español",LocalDate.of(1985, 11, 30),"Calle Mayor 45",
                		 "Madrid","28013","España","+34 91 123 4567","+34 600 987 654",
                			"juan.perez@example.com");
    }
    
    @Test
    void testGetAllClients() throws Exception {
    	
    	clientList = Arrays.asList(testClient, testClient2);
    	when(clientService.showAllClients()).thenReturn(clientList);
    	
    	mockMvc.perform(get("/api/clients"))
        .andExpect(status().isOk())                        
        .andExpect(content().contentType("application/json")) 
        .andExpect(jsonPath("$", hasSize(2)))                
        .andExpect(jsonPath("$[1].id", is("123")))         
        .andExpect(jsonPath("$[1].name", is("Juan Pérez"))) 
        .andExpect(jsonPath("$[1].email", is("juan.perez@example.com"))) 
        .andExpect(jsonPath("$[0].id", is("456")))          
        .andExpect(jsonPath("$[0].name", is("Ana Martínez"))) 
        .andExpect(jsonPath("$[0].email", is("ana.martinez@example.com")));
    } 
    
    @Test
    void testGetClientById() throws Exception {
    	
    	when(clientService.findClientById("456")).thenReturn(Optional.of(testClient));
    	
    	mockMvc.perform(get("/api/clients/{id}", "456"))
    	.andExpect(status().isOk())                        
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id", is("456")))          
        .andExpect(jsonPath("$.name", is("Ana Martínez"))) 
        .andExpect(jsonPath("$.email", is("ana.martinez@example.com")));
    }
    
    @Test
    void testCretedClient() throws JsonProcessingException, Exception {
    	Client saveTestClient = testClient;
    	
    	when(clientService.addNewClient(any(Client.class))).thenReturn(saveTestClient);
    	
    	mockMvc.perform(post("/api/clients")
    			.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClient)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is("456")))
                .andExpect(jsonPath("$.name", is("Ana Martínez")))
                .andExpect(jsonPath("$.email", is("ana.martinez@example.com")));
    }
    
    @Test
    void testUpdatedClient() throws JsonProcessingException, Exception {
    	
    	Client updatedClient= new Client("123","Jonathan Pulido",'M',"Español",LocalDate.of(1985, 11, 30),"Calle Mayor 45",
       		 "Madrid","28013","España","+34 91 123 4567","+34 600 987 654",
       			"jonathan.pulido@example.com");
    	
    	
    	when(clientService.updateClient(eq("123"), any(Client.class))).thenReturn(updatedClient);
    	
    	mockMvc.perform(put("/api/clients/{id}" , "123")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(updatedClient)))
    			.andExpect(status().isOk())
    			.andExpect(content().contentType("application/json"))
    			.andExpect(jsonPath("$.id", is("123")))
    			.andExpect(jsonPath("$.name", is("Jonathan Pulido")))
    			.andExpect(jsonPath("$.email", is("jonathan.pulido@example.com")));
    			
    }  
    
    @Test
    void testDeleteClient() throws Exception {
    	
    	when(clientService.deleteClientById(eq("123"))).thenReturn(true);
    	
    	mockMvc.perform(delete("/api/clients/{id}" , "123"))
    			.andExpect(status().isNoContent());		
    }
    
    
    
    
    
}
