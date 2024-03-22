package com.tool.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RentalApplication {


	public static void main(String[] args) {
		SpringApplication.run(RentalApplication.class, args);
	}
}
