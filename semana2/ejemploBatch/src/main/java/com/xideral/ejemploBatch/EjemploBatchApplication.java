package com.xideral.ejemploBatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.xideral.ejemploBatch.models.Persona;
import com.xideral.ejemploBatch.repositorys.PersonaRepository;

import ch.qos.logback.core.net.SyslogOutputStream;

@SpringBootApplication
public class EjemploBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(EjemploBatchApplication.class, args);
	}
	
	/*@Bean
	public CommandLineRunner testConexion(PersonaRepository personaRepo) {
		return arg -> {
			Persona p = new Persona();
			p.setNombre("Jonathan Alexandro Pulido Estrada");
			p.setEdad(29);
			p.setNacionalidad("mexicano");
			personaRepo.save(p);
			System.out.println(p.getNombre()+" fué almacenado en la base de datos");
		};
	}*/

}
