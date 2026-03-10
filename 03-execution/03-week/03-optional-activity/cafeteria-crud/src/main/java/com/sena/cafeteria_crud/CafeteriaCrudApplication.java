package com.sena.cafeteria_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CafeteriaCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeteriaCrudApplication.class, args);
	}

}
