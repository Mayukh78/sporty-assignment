package com.example.sporty.sporty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SportyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportyApplication.class, args);
	}

}
