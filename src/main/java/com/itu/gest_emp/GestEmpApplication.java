package com.itu.gest_emp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class GestEmpApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestEmpApplication.class, args);
	}

}
