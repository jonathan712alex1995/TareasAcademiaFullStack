package com.banco.clientes.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.banco.clientes.model.Client;
import com.banco.clientes.repository.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    //metodos CRUD
    //AGREGAR
    public Client addNewClient(Client client) {
        return clientRepository.save(client);
    }

    //LISTAR
    public List<Client> showAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> findClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Optional<Client> findClientByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    public Optional<Client> findClientByCellphone(String cellphone) {
        return clientRepository.findByCellphone(cellphone);
    }

    //ACTUALIZAR
    public Client updateClient(String id, Client updatedClient) {
        Optional<Client> existClient = clientRepository.findById(id);
        if(existClient.isPresent()) {
            Client client = existClient.get();
            client.setName(updatedClient.getName());
            client.setGender(updatedClient.getGender());
            client.setNationality(updatedClient.getNationality());
            client.setBirthDate(updatedClient.getBirthDate());
            client.setAddress(updatedClient.getAddress());
            client.setCity(updatedClient.getCity());
            client.setPostalCode(updatedClient.getPostalCode());
            client.setCountry(updatedClient.getCountry());
            client.setPhone(updatedClient.getPhone());
            client.setCellphone(updatedClient.getCellphone());
            client.setEmail(updatedClient.getEmail());
            return clientRepository.save(client);
        }
        return null;
    }

    public boolean deleteClientById(String id) {
        if(clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void deleteAllClients() {
        clientRepository.deleteAll();
    }

    public long countClients() {
        return clientRepository.count();
    }
}