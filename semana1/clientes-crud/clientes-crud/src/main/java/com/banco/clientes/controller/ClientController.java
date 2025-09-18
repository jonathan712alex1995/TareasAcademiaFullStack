package com.banco.clientes.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.clientes.model.Client;
import com.banco.clientes.service.ClientService;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientService clientService;

    //Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.showAllClients();
        return ResponseEntity.ok(clients);
    }

    //Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable String id) {
        Optional<Client> client = clientService.findClientById(id);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        }
        return ResponseEntity.notFound().build();
    }

    //Crear nuevo cliente
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        try {
            Client newClient = clientService.addNewClient(client);
            return ResponseEntity.status(HttpStatus.CREATED).body(newClient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id, @RequestBody Client client) {
        Client updatedClient = clientService.updateClient(id, client);
        if (updatedClient != null) {
            return ResponseEntity.ok(updatedClient);
        }
        return ResponseEntity.notFound().build();
    }

    //Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        boolean deleted = clientService.deleteClientById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    //Buscar por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Optional<Client> client = clientService.findClientByEmail(email);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        }
        return ResponseEntity.notFound().build();
    }

    //Contar clientes
    @GetMapping("/count")
    public ResponseEntity<Long> countClients() {
        long count = clientService.countClients();
        return ResponseEntity.ok(count);
    }
}