package com.banco.clientes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.banco.clientes.model.Client;
import com.banco.clientes.repository.ClientRepository;


@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

	@Mock
	private ClientRepository clientRepository;
	
	@InjectMocks
	private ClientService clientService;
	
	private Client testClient;
	
	@BeforeEach
	void initSetup() {
		testClient = new Client();
        testClient.setId("123");
        testClient.setName("Juan Pérez");
        testClient.setEmail("juan@email.com");
        testClient.setPhone("555-1234");
        testClient.setGender('M');
        testClient.setNationality("Mexicana");
        testClient.setBirthDate(LocalDate.of(1990, 1, 1));
	}
	
	@Test
    void testAddNewClient() {
    
        when(clientRepository.save(testClient)).thenReturn(testClient);

        Client result = clientService.addNewClient(testClient);

        assertNotNull(result);
        assertEquals("Juan Pérez", result.getName());
        assertEquals("juan@email.com", result.getEmail());
 
        verify(clientRepository, times(1)).save(testClient);
    }
	
	@Test
	void testFindClientById_ClientExists() {
	    // ARRANGE
	    String clientId = "123";
	    when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));

	    // ACT
	    Optional<Client> result = clientService.findClientById(clientId);

	    // ASSERT
	    assertTrue(result.isPresent());
	    assertEquals("Juan Pérez", result.get().getName());
	    verify(clientRepository, times(1)).findById(clientId);
	}
	
	@Test
	void testFindClientById_ClientNotExists() {
	    // ARRANGE
	    String clientId = "999";
	    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

	    // ACT
	    Optional<Client> result = clientService.findClientById(clientId);

	    // ASSERT
	    assertFalse(result.isPresent());
	    verify(clientRepository, times(1)).findById(clientId);
	}
	
	@Test
	void testShowAllClients() {
	    // ARRANGE
	    Client client2 = new Client();
	    client2.setId("456");
	    client2.setName("María García");
	    client2.setEmail("maria@email.com");

	    List<Client> clientList = Arrays.asList(testClient, client2);
	    when(clientRepository.findAll()).thenReturn(clientList);

	    // ACT
	    List<Client> result = clientService.showAllClients();

	    // ASSERT
	    assertNotNull(result);
	    assertEquals(2, result.size());
	    assertEquals("Juan Pérez", result.get(0).getName());
	    assertEquals("María García", result.get(1).getName());
	    verify(clientRepository, times(1)).findAll();
	}
	
	@Test
	void testUpdateClient_Success() {
	    // ARRANGE
	    String clientId = "123";
	    Client updatedClient = new Client();
	    updatedClient.setName("Juan Carlos Pérez");
	    updatedClient.setEmail("juancarlos@email.com");
	    updatedClient.setPhone("555-9999");

	    when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
	    when(clientRepository.save(any(Client.class))).thenReturn(testClient);

	    // ACT
	    Client result = clientService.updateClient(clientId, updatedClient);

	    // ASSERT
	    assertNotNull(result);
	    verify(clientRepository, times(1)).findById(clientId);
	    verify(clientRepository, times(1)).save(any(Client.class));
	}

	@Test
	void testUpdateClient_ClientNotFound() {
	    // ARRANGE
	    String clientId = "999";
	    Client updatedClient = new Client();
	    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

	    // ACT
	    Client result = clientService.updateClient(clientId, updatedClient);

	    // ASSERT
	    assertNull(result);
	    verify(clientRepository, times(1)).findById(clientId);
	    verify(clientRepository, never()).save(any(Client.class));
	}
	
	
}
