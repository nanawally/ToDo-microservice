package com.nanawally.ToDo_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToDoMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDoMicroserviceApplication.class, args);
        System.out.println("ToDo Microservice started");
	}
}
