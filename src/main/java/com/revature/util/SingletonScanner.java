package com.revature.util;


import java.util.Scanner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SingletonScanner {
	
	@Bean
	public Scanner scan() {
		return new Scanner(System.in);
	}
}