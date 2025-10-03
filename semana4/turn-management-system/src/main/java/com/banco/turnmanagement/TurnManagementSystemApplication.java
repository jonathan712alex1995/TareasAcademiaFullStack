package com.banco.turnmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableScheduling;


@Modulith
@SpringBootApplication
@EnableScheduling
public class TurnManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurnManagementSystemApplication.class, args);
	}

}
