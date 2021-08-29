package com.revature.services;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.revature.beans.Activity;
import com.revature.beans.Car;
import com.revature.beans.Flight;
import com.revature.beans.Hotel;
import com.revature.beans.Reservation;
import com.revature.beans.User;
import com.revature.beans.Vacation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
	private static MultiValueMap<String, String> myCookies = new LinkedMultiValueMap<String, String>();
	String aid = null;
	
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
	}	
	
	public void printAllActivitiesbyLocation(String loc) {
		WebClient webClient = WebClient.create();
		Flux<Activity> activities = webClient.get()
				.uri("http://localhost:8080/activities/" + loc)
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Activity.class);
		activities.subscribe( (act) -> {
			System.out.println(act);
		});
	}	
	
	public Mono<Activity> getActivityById(String id) {
			WebClient webClient = WebClient.create();
			Flux <Activity> activities = webClient.get()
					.uri("http://localhost:8080/activities/" + id)
					.cookies(cookies -> cookies.addAll(myCookies))
					.retrieve()
					.bodyToFlux(Activity.class);
			return activities.single();
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
	
	public Mono<Activity> addActivity(String Id) {
		WebClient webClient = WebClient.create();
		 
		Mono<Activity> act = getActivityById(Id);

		return webClient.post()
				.uri("http://localhost:8080/activities")
				.body(act, Activity.class)
				.exchangeToMono(r ->  {
				for (String key: r.cookies().keySet()) {
					myCookies.put(key, Arrays.asList(r.cookies().get(key).get(0).getValue()));
				}
				return r.bodyToMono(Activity.class);				
			});
	}
	
	public Flux<Car> getCars(String destination) {
		WebClient webClient = WebClient.create();
		String uri = "http://localhost:8080/cars/"+destination;
		System.out.println(uri);
		Flux<Car> res = webClient.get()
				.uri(uri)
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Car.class);
		res.subscribe(c -> System.out.println(c));
		return res;
	}
	
	public void addReservation(Reservation r) {
		WebClient webClient = WebClient.create();
		webClient.post()
				.uri("http://localhost:8080/reservations/")
				.cookies(cookies -> cookies.addAll(myCookies))
				.body(Mono.just(r),Reservation.class)
				.retrieve()
				.bodyToMono(Reservation.class)
				.subscribe();
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
	
	
	public Flux<Flight> getFlights(String destination) {
		WebClient webClient = WebClient.create();
		String uri = "http://localhost:8080/flights/"+destination;
		System.out.println(uri);
		Flux<Flight> res = webClient.get()
				.uri(uri)
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Flight.class);
		res.subscribe(f -> System.out.println(f));
		return res;
	}
	
	public Flux<Hotel> getHotels(String destination) {
		WebClient webClient = WebClient.create();
		String uri = "http://localhost:8080/hotels/"+destination;
		System.out.println(uri);
		Flux<Hotel> res = webClient.get()
				.uri(uri)
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Hotel.class);
		res.subscribe(h -> System.out.println(h));
		return res;
	}
	
	public void deleteAccount(User u) {
		WebClient webClient = WebClient.create();
			webClient.delete()
				.uri("http://localhost:8080/users/"+u.getUsername())
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.toBodilessEntity()
				.subscribe();
	}


}
