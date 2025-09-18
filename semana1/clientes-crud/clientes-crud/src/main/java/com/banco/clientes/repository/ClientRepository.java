package com.banco.clientes.repository;

import com.banco.clientes.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    
    // Métodos para realizar busqueda
    Optional<Client> findByEmail(String email);
    List<Client> findByNationality(String nationality);
    List<Client> findByCity(String city);
    List<Client> findByNameContainingIgnoreCase(String name);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByCellphone(String cellphone);
}