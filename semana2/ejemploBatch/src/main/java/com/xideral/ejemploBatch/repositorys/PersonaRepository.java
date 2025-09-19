package com.xideral.ejemploBatch.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xideral.ejemploBatch.models.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long>{

}
