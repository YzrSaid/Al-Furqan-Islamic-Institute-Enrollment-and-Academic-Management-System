package com.example.testingLogIn;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCaching
@EnableAsync
@SpringBootApplication
public class TestingLogInApplication extends SpringBootServletInitializer
 									{
	public static void main(String[] args) {
		SpringApplication.run(TestingLogInApplication.class, args);
	}
}
