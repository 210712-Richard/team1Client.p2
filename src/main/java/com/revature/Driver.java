package com.revature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.revature.services.UserService;

@SpringBootApplication
public class Driver {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Driver.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		
		testMethod(application, args);
	}
	
	private static void testMethod(SpringApplication application, String[] args) {
		ApplicationContext ctx = application.run(args);
		UserService us = ctx.getBean(UserService.class);
		Menu menu = new Menu();
		//us.loginAsTest();
		//us.printAllActivities();
		menu.start();
	}
}
