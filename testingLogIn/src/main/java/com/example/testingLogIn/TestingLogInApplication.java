package com.example.testingLogIn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TestingLogInApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestingLogInApplication.class, args);
	}

}
