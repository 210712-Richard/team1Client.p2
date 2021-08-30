package com.revature.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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
	
	public Flux<Activity> printAllActivitiesbyLocation(String loc) {
		WebClient webClient = WebClient.create();
		return webClient.get()
				.uri("http://localhost:8080/activities/" + loc)
				.cookies(cookies -> cookies.addAll(myCookies))
				.retrieve()
				.bodyToFlux(Activity.class);
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
		
	}
	
	private void printVacation(Vacation vac) {
		System.out.println("Vacation ID: " + vac.getId());
		System.out.println("Vacation Location: " + vac.getDestination());
		System.out.println("Vacation Date: " + vac.getStartTime());
		System.out.println("Vacation End time: " + vac.getEndTime());
		System.out.println("Vacation Status: " 
		+ (vac.getEndTime().isBefore(LocalDateTime.now()) ? "No longer available\n" : "Open\n"));
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
	

	public Mono<Activity> addActivity(String username, UUID id, Activity act) {
		WebClient webClient = WebClient.create();

		return webClient.post()
				.uri("http://localhost:8080/users/"+username+"/vacations/"+id.toString()+"/activities")
				.body(Mono.just(act), Activity.class)
				.cookies(cookies -> cookies.addAll(myCookies))
				.exchangeToMono(r ->{
					if (r.statusCode().is2xxSuccessful()) {
						System.out.println("Activity Added");
						return r.bodyToMono(Activity.class);
					}
					else {
						System.out.println("Error adding activity. Please try again.");
						return Mono.empty();
					}
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

	
	public Mono<Vacation> createVacation(String username, Vacation vac) {
		WebClient webClient = WebClient.create();
		return webClient.post()
		.uri("http://localhost:8080/users/"+username+"/vacations")
		.body(Mono.just(vac), Vacation.class)
		.cookies(cookies -> cookies.addAll(myCookies))
		.exchangeToMono(r -> {
			if (r.statusCode().is2xxSuccessful()) {
				System.out.println("Vacation created");
				return r.bodyToMono(Vacation.class);
			}
			else {
				System.out.println("Error creating vacation. Please try again");
				return Mono.just(new Vacation());
			}
		});
	}
	
	public Mono<Reservation> rescheduleReservation(Reservation update, UUID id){
		WebClient webClient = WebClient.create();
		return webClient.patch()
		.uri("http://localhost:8080/reservations/" + id.toString())
		.body(Mono.just(update), Reservation.class)
		.cookies(cookies -> cookies.addAll(myCookies))
		.exchangeToMono(r -> {
			if (r.statusCode().is2xxSuccessful()) {
				System.out.println("Reservation added successfully");
				return r.bodyToMono(Reservation.class);
			} else if (r.statusCode().equals(HttpStatus.CONFLICT)) {
				System.out.println("The reservations are maxed out for that time block.");
			} else {
				System.out.println("Error with your reservation. Please try again.");
			}
			return Mono.empty();
		});
	}


}
