package com.banco.clientes.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.clientes.model.Card;
import com.banco.clientes.service.CardService;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    @Autowired
    private CardService cardService;

    //Obtener todas las tarjetas
    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        List<Card> cards = cardService.showAllCards();
        return ResponseEntity.ok(cards);
    }

    // Obtener tarjeta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable String id) {
        Optional<Card> card = cardService.findCardById(id);
        if (card.isPresent()) {
            return ResponseEntity.ok(card.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Crear nueva tarjeta
    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        try {
            Card newCard = cardService.addNewCard(card);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar tarjeta
    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable String id, @RequestBody Card card) {
        Card updatedCard = cardService.updateCard(id, card);
        if (updatedCard != null) {
            return ResponseEntity.ok(updatedCard);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar tarjeta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable String id) {
        boolean deleted = cardService.deleteCardById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    //Obtener tarjetas por cliente
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Card>> getCardsByClient(@PathVariable String clientId) {
        List<Card> cards = cardService.findCardsByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    //Cambiar estado de tarjeta
    @PutMapping("/{id}/status")
    public ResponseEntity<String> changeCardStatus(@PathVariable String id, @RequestBody String status) {
        boolean updated = cardService.disableClientCard(id, status.replace("\"", ""));
        if (updated) {
            return ResponseEntity.ok("Estado actualizado correctamente");
        }
        return ResponseEntity.badRequest().body("Error al actualizar estado o tarjeta no encontrada"+id+"---"+status);
    }
}