package com.revature.services;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.revature.beans.Activity;
import com.revature.beans.User;
import com.revature.beans.Vacation;

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

	}
	
	public Mono<Void> logout() {
		WebClient webClient = WebClient.create();
		return webClient.delete()
				.uri("http://localhost:8080/users")
				.cookies(cookies -> cookies.addAll(myCookies))
				.exchangeToMono(r -> {
					if (r.statusCode().is2xxSuccessful()) {
						return Mono.empty();
					}
					else {
						return Mono.error(new Throwable("Logout failed"));
					}
				});
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
	
	public Mono<Vacation> getVacation(User u, UUID id) {
		WebClient webClient = WebClient.create();
		String uri = "http://localhost:8080/users/"+u.getUsername()+"/vacations/"+id;
		Mono<Vacation> res = webClient.get()
				.uri(uri)
				.cookies(cookies -> cookies.addAll(myCookies))
				.exchangeToMono(v -> {
					return v.bodyToMono(Vacation.class);
				});
		return res;
	}

}
