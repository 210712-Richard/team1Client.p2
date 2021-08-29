package com.revature.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.revature.beans.Activity;
import com.revature.beans.Reservation;
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
	
	public Flux<Vacation> getUsersVacations(User user, List<Vacation> vac_list) {
		String baseUrl = "http://localhost:8080";
		WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
		Flux<Vacation> vacs = Flux.empty();		
		 user.getVacations().forEach((vid) -> {
			Vacation vac = webClient.get()
					.uri(uriBuilder ->
					uriBuilder
					.path("/users/{username}/vacations/{vacId}")
					.build(user.getUsername(), vid.toString()))
					.cookies(cookies -> cookies.addAll(myCookies))
					.retrieve()
					.bodyToMono(Vacation.class).concatWith(vacs)
					.blockFirst();
			vac_list.add(vac);
			printVacation(vac);
			});
		 
		 return vacs;		
	}
	
	public void updateReservation(Reservation res) {
		String baseUrl = "http://localhost:8080";
		WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
			
		Reservation updatedRes = webClient.put()
		.uri(uriBuilder -> uriBuilder
				.path("/reservations/{resId}/status")
				.build(res.getId()))
		.cookies(cookies -> cookies.addAll(myCookies))
		.body(Mono.just(res), Reservation.class)
		.retrieve()
		.bodyToMono(Reservation.class)
		.block();
		
		System.out.println("Reservation updated!");
		System.out.println("Reservation :" + updatedRes);
		
	}
	
	private void printVacation(Vacation vac) {
		System.out.println("Vacation ID: " + vac.getId());
		System.out.println("Vacation Location: " + vac.getDestination());
		System.out.println("Vacation Date: " + vac.getStartTime());
//		System.out.println("Vacation End time: " + vac.getEndTime());
		System.out.println("Vacation Status: " 
		+ (vac.getEndTime().isBefore(LocalDateTime.now()) ? "No longer available\n" : "Open\n"));
	}

}
