package com.revature.services;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.revature.Menu;
import com.revature.beans.Activity;
import com.revature.beans.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
	private static MultiValueMap<String, String> myCookies = new LinkedMultiValueMap<String, String>();

	
	public Mono<User> login(User u) {
		WebClient webClient = WebClient.create();
		return webClient.post()
				.uri("http://localhost:8080/users")
				.body(Mono.just(u),User.class)
				.exchangeToMono(r ->  {
					for (String key: r.cookies().keySet()) {
				        myCookies.put(key, Arrays.asList(r.cookies().get(key).get(0).getValue()));
				      }
					return r.bodyToMono(User.class);
					
				});
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}
	
	public void printAllActivities() {
		WebClient webClient = WebClient.create();
		Flux<Activity> activities = webClient.get()
				.uri("http://localhost:8080/activities/Los Angeles, CA")
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Activity.class);
		activities.subscribe( (act) -> {
			System.out.println(act);
		});
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	public void register(User u) {
		WebClient webClient = WebClient.create();
		webClient.put()
				.uri("http://localhost:8080/users/"+u.getUsername())
				.body(Mono.just(u),User.class)
				.retrieve()
				.bodyToMono(User.class)
				.subscribe(user -> System.out.println(user));
	}

}
