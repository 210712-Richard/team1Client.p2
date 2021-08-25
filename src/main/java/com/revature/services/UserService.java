package com.revature.services;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.revature.beans.Activity;
import com.revature.beans.User;
import com.sun.tools.sjavac.Log;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
	
	public void loginAsTest() {
		WebClient webClient = WebClient.create();
		User u = new User();
		u.setUsername("test");
		u.setPassword("password");
		Mono<User> user = webClient.post()
				.uri("http://localhost:8080/users")
				.bodyValue(u)
				.retrieve()
				.bodyToMono(User.class);
	}
	
	public void printAllActivities() {
		WebClient webClient = WebClient.create();
		Flux<Activity> activities = webClient.get()
				.uri("http://localhost:8080/activities/Los Angeles, CA)")
				.accept(MediaType.APPLICATION_NDJSON)
				.retrieve()
				.bodyToFlux(Activity.class);
//				.onErrorResume(e -> {
//					e.printStackTrace();
//					return Flux.empty();
//				});
		activities.subscribe( (act) -> {
			System.out.println(act);
		});
		
		int i = 0;
		while(true) {
			System.out.println("hello");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
			if(i>10) {
				break;
			}
		}
	}

}
