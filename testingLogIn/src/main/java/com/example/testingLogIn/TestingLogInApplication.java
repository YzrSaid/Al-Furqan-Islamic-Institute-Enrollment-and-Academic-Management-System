package com.example.testingLogIn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class TestingLogInApplication {

	public static void main(String[] args) {
		List<?> goAdd = new ArrayList<>();
		SpringApplication.run(TestingLogInApplication.class, args);
	}
}
