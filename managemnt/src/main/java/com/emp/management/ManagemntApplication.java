package com.emp.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManagemntApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagemntApplication.class, args);
	}

}
