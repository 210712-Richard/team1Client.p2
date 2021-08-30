package com.revature;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.beans.Activity;
import com.revature.beans.Car;
import com.revature.beans.Flight;
import com.revature.beans.Hotel;
import com.revature.beans.Reservation;
import com.revature.beans.ReservationStatus;
import com.revature.beans.ReservationType;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.beans.Vacation;
import com.revature.services.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;



@Component
public class Menu {
	@Autowired
	private UserService us;
	@Autowired
	private Scanner scan;

	private static User loggedUser;

	private static List<Vacation> vaclist;
	private static List<Reservation> reslist;

	public static void setLoggedUser(User u) {
		loggedUser = u;
	}
	
	//public static void setActivity(Activity activity) {
	//	activities = activity;
	//}
	public void start() {
		while (true) {
			switch (mainMenuInput()) {
			case "1":
				register();
				break;
			case "2":
				login();
				break;
			case "3":
				return;
			}
		}
	}

	private String mainMenuInput() {
		// Using the scanner, asks the user for one of the above 3 mainmenu choices
		System.out.println("Vacationeer!\n" + "1: Register for a new account\n" + "2: Login\n" + "3: Quit");
		return scan.nextLine().trim();
	}

	private void register() {
		// Takes input and registers user with a request based on that input
		User u = new User();
		System.out.println("Choose a username:");
		u.setUsername(scan.nextLine().trim());
		System.out.println("Choose a password:");
		u.setPassword(scan.nextLine().trim());
		System.out.println("Enter your email:");
		u.setEmail(scan.nextLine().trim());
		System.out.println("Enter your first name:");
		u.setFirstName(scan.nextLine().trim());
		System.out.println("Enter your last name:");
		u.setLastName(scan.nextLine().trim());
		LocalDate birthday = null;
		while (birthday == null) {
			System.out.println("Enter your birthday (YYYY-MM-DD):");
			birthday = LocalDate.parse(scan.nextLine().trim());
		}
		u.setBirthday(birthday);
		System.out.println("Are you a staff member? (y/n):");
		String type = scan.nextLine().trim();
		if (type.equals("n")) {
			u.setType(UserType.VACATIONER);
		} else {
			System.out.println("Are you flight staff, hotel staff, or car staff? (f/h/c)");
			switch (scan.nextLine().trim()) {
			case "f":
				u.setType(UserType.FLIGHT_STAFF);
				break;
			case "h":
				u.setType(UserType.HOTEL_STAFF);
				break;
			case "c":
				u.setType(UserType.CAR_STAFF);
				break;
			}
		}
		us.register(u);

	}

	private void login() {
		// Takes input and logs in user with a request based on that input
		User u = new User();
		System.out.println("Enter your username");
		u.setUsername(scan.nextLine().trim());
		System.out.println("Enter your password");
		u.setPassword(scan.nextLine().trim());
		loggedUser = us.login(u).map(user -> {
			System.out.println("Login Successful");
			return user;
		}).block();
		loginMenu();
	}

	private void loginMenu() {
		if (loggedUser == null) {
			System.out.println("Incorrect login credentials");
			return;
		}
		while (true) {
			switch (loginMenuInput()) {
			case "1":
				createVacation();
				break;
			case "2":
				chooseVacationMenu();
				break;
			case "3":
				if(loggedUser.getType() == UserType.VACATIONER)
					doVacationMenu();					
				else
					getReservations();

				break;
			case "4":
				logout();
				return;
			case "5":
				if (deleteAccount()) {
					return;
				}
				break;
				continue;
			case "6":
				rescheduleReservationStaffMenu();
				break;
      default: System.out.println("Invalid input. Try again");
      contitnue;
			}
		}
	}

	private void getReservations() {
		reslist = new ArrayList<>();
		us.getReservationsByType().flatMap(res -> {
			reslist.add(res);
			System.out.println("New reservation added. Res ID: " + res.getId());
			return Flux.just(res);
		}).blockLast();
		
		modifyReservationStaffMenu();
	}

	private void modifyReservationStaffMenu() {
		if(reslist == null) {
			reslist = new ArrayList<>();
			us.getReservationsByType().flatMap(res -> {
				reslist.add(res);
				System.out.println("New reservation added. Res ID: " + res.getId());
				return Flux.just(res);
			}).blockLast();;
		}
		
		if(reslist.size() == 0) {
			System.out.println("No " + loggedUser.getType().toString().split("_")[0] +
					" reservations available\n\n");
			return;
		}
		
		modifyReservationsMenuInput(reslist);
	}

	private String loginMenuInput() {
		// Using the scanner, asks the user for one of the above 5 loginmenu choices
		System.out.println("Welcome, " + loggedUser.getUsername());
		System.out.println("1: Create a new Vacation");
		System.out.println("2: Edit a vacation");
		if(loggedUser.getType() != UserType.VACATIONER)
			System.out.println("3: Modify status for a reservation");
		else
			System.out.println("3: Go on a vacation!");
		
		System.out.println("4: Logout");
		System.out.println("5: Delete Account");
    System.out.println("6. Reschedule a Reservation");

		return scan.nextLine().trim();
	}

	private void createVacation() {
		// Takes input, creates vacation based on input, redirects to edit that vacation
		System.out.println("Where will this vacation be at?");
		String location = scan.nextLine().trim();

		System.out.println("What day will this vacation start?");
		System.out.println("\tFormat: YYYY-MM-DD");

		String date = scan.nextLine().trim();
		System.out.println("What time?");
		System.out.println("\tFormat: HH:MM");
		String time = scan.nextLine().trim();

		Integer duration = -1;
		while (duration <= 0) {
			System.out.println("How many days will you be on vacation?");
			duration = Integer.parseInt(scan.nextLine());
			if (duration <= 0) {
				System.out.println("Please enter a postive amount");
			}
		}
		Integer partySize = -1;
		while (partySize <= 0) {
			System.out.println("How many people will be coming with you?");
			partySize = Integer.parseInt(scan.nextLine());
			if (partySize <= 0) {
				System.out.println("Please enter a postive amount");
			}
		}
		Vacation vac = new Vacation();
		vac.setDestination(location);
		LocalDate localDate = LocalDate.parse(date);
		LocalTime localTime = LocalTime.parse(time);
		LocalDateTime startTime = LocalDateTime.of(localDate, localTime);
		vac.setStartTime(startTime);
		vac.setPartySize(partySize);
		vac.setDuration(duration);
		vac = us.createVacation(loggedUser.getUsername(), vac).block();
		if (vac.getId() != null) {
			loggedUser.getVacations().add(vac.getId());
		}

	}

	private void chooseVacationMenu() {
		// COMPLEX
		// Shows the user all their vacations,
		// and lets them choose one to edit
		Flux<Vacation> vacations = Flux.empty();
		for (int i = 0; i < loggedUser.getVacations().size(); i++) {
			vacations = Flux.concat(vacations, us.getVacation(loggedUser, loggedUser.getVacations().get(i)));
		}
		Flux<Tuple2<Long, Vacation>> vacationsOrdered = vacations.index();
		vacationsOrdered.subscribe(t -> {
			System.out.println("Enter " + (t.getT1() + 1) + " to edit the vacation to " + t.getT2().getDestination()
					+ " at " + t.getT2().getStartTime());
		});
		Long choiceIndex = Long.parseLong(scan.nextLine().trim());
		Vacation choice = vacationsOrdered.filter(t -> t.getT1().equals(choiceIndex - 1)).blockFirst().getT2();
		editVacationMenu(choice);

	}

	private void editVacationMenu(Vacation v) {
		while (true) {
			switch (editVacationInput(v)) {
			case "1":
				addCar(v);
				break;
			case "2":
				addHotel(v);
				break;
			case "3":
				addFlight(v);
				break;
			case "4":
				addActivityMenu(v);
				break;
			case "5":
				rescheduleReservationVacationerMenu(v);
				break;
			case "6":
				return;
			}
		}

	}

	private String editVacationInput(Vacation v) {
		// Using the scanner, asks the user for one of the above 6 editvacationmenu
		// choices
		System.out.println(
				"Edit this vacation to " + v.getDestination() + "\n" + "1: Rent a car\n" + "2: Reserve a hotel\n"
						+ "3: Book a flight\n" + "4: Plan an activity\n" + "5: Reschedule\n" + "6: Back");
		return scan.nextLine().trim();
	}

	private void addCar(Vacation v) {
		Flux<Car> cars = us.getCars(v.getDestination());

		Flux<Tuple2<Long, Car>> carsOrdered = cars.index();
		carsOrdered.subscribe(t -> {
			System.out.println((t.getT1() + 1) + ": rent this " + t.getT2().getMake() + " " + t.getT2().getModel()
					+ " for " + t.getT2().getCostPerDay() + " per day");
		});
		Long choiceIndex = Long.parseLong(scan.nextLine().trim());
		Car choice = carsOrdered.filter(t -> t.getT1().equals(choiceIndex - 1)).blockFirst().getT2();
		Reservation r = new Reservation();
		r.setType(ReservationType.CAR);
		r.setReservedId(choice.getId());
		r.setVacationId(v.getId());
		r.setUsername(loggedUser.getUsername());
		r.setReservedName(choice.getMake() + " " + choice.getModel());
		r.setStartTime(v.getStartTime());
		r.setCost(choice.getCostPerDay());
		r.setDuration(v.getDuration());
		r.setStatus(ReservationStatus.AWAITING);
		us.addReservation(r);

	}

	private void addHotel(Vacation v) {
		Flux<Hotel> hotels = us.getHotels(v.getDestination());

		Flux<Tuple2<Long, Hotel>> hotelsOrdered = hotels.index();
		hotelsOrdered.subscribe(h -> {
			System.out.println((h.getT1() + 1) + ": reserve a room at the " + h.getT2().getName() + " for "
					+ h.getT2().getCostPerNight() + " per night");
		});
		Long choiceIndex = Long.parseLong(scan.nextLine().trim());
		Hotel choice = hotelsOrdered.filter(t -> t.getT1().equals(choiceIndex - 1)).blockFirst().getT2();
		Reservation r = new Reservation();
		r.setType(ReservationType.HOTEL);
		r.setReservedId(choice.getId());
		r.setVacationId(v.getId());
		r.setUsername(loggedUser.getUsername());
		r.setReservedName(choice.getName());
		r.setStartTime(v.getStartTime());
		r.setCost(choice.getCostPerNight());
		r.setDuration(v.getDuration());
		r.setStatus(ReservationStatus.AWAITING);
		us.addReservation(r);
	}

	private void addFlight(Vacation v) {
		// Gets user input for which flight to add
		Flux<Flight> flights = us.getFlights(v.getDestination());

		Flux<Tuple2<Long, Flight>> flightsOrdered = flights.index();
		flightsOrdered.subscribe(f -> {
			System.out.println((f.getT1() + 1) + ": buy a seat on an " + f.getT2().getAirline() + " flight for "
					+ f.getT2().getTicketPrice());
		});
		Long choiceIndex = Long.parseLong(scan.nextLine().trim());
		Flight choice = flightsOrdered.filter(t -> t.getT1().equals(choiceIndex - 1)).blockFirst().getT2();
		Reservation r = new Reservation();
		r.setType(ReservationType.FLIGHT);
		r.setReservedId(choice.getId());
		r.setVacationId(v.getId());
		r.setUsername(loggedUser.getUsername());
		r.setReservedName(choice.getAirline());
		r.setStartTime(v.getStartTime());
		r.setCost(choice.getTicketPrice());
		r.setDuration(v.getDuration());
		r.setStatus(ReservationStatus.AWAITING);
		us.addReservation(r);
	}

	
	private void addActivityMenu(Vacation v) {
		
		addActivity(v);
	}

	private Boolean addActivity(Vacation vac) {
		// COMPLEX
		// Shows the user all activities at the given location,
		// and lets them choose one to add
		Flux<Activity> actFlux = us.printAllActivitiesbyLocation(vac.getDestination());
		Flux<Tuple2<Long, Activity>> listFlux = actFlux.index();	
		listFlux.subscribe(t -> {
			System.out.println("Enter " + (t.getT1() + 1) + ". to add: " + t.getT2().getName());
			System.out.println("\t" + t.getT2().getDescription());
			System.out.println("\tCost:" + t.getT2().getCost());
			System.out.println();
		});
		Long choiceIndex = Long.parseLong(scan.nextLine().trim());
		Activity choice = listFlux.filter(t -> t.getT1().equals(choiceIndex - 1))
				.blockFirst().getT2();
		return us.addActivity(loggedUser.getUsername(), vac.getId(), choice)
				.map(a -> true)
				.switchIfEmpty(Mono.just(false))
				.block();
	}

	private void rescheduleReservationVacationerMenu(Vacation vac) {
		// VERY COMPLEX
		// With one choice for each reservation in the selected vacation,
		// each choice with allow the user to edit its corresponding reservation
		vac = us.getVacation(loggedUser, vac.getId()).block();
		Reservation selection = null;
		while(selection == null) {
			System.out.println("Please Choose a Reservation from Below:\n");
			int i = 1;
			for (Reservation res : vac.getReservations()) {
				System.out.println(i + ". " + res.getReservedName() +" at " + res.getStartTime());
				System.out.println("Duration (in days): " + res.getDuration());
				System.out.println("Cost: " + res.getCost());
				System.out.println("Status: " + res.getStatus());
				System.out.println();
				i++;
			}
			System.out.println("Please select which one to reschedule");
			int select = Integer.parseInt(scan.nextLine().trim());
			if (select <= 0 || select > vac.getReservations().size()) {
				System.out.println("That selection is invalid. Please try again.");
			} else {
				selection = vac.getReservations().get(select-1);
			}
		}
		
		rescheduleReservation(selection, vac.getDestination());
	}
	
	private void rescheduleReservationStaffMenu() {
		Flux<Reservation> resFlux = us.getReservationsByType();
		Flux<Tuple2<Long, Reservation>> listFlux = resFlux.index();
		
		listFlux.subscribe(t -> {
			Long i = t.getT1() + 1;
			Reservation res = t.getT2();
			System.out.println(i + ". " + res.getReservedName() +" at " + res.getStartTime());
			System.out.println("Duration (in days): " + res.getDuration());
			System.out.println("Cost: " + res.getCost());
			System.out.println("Status: " + res.getStatus());
			System.out.println();
		});
		Long select = Long.parseLong(scan.nextLine().trim());
		Reservation selection = listFlux.filter(t -> t.getT1() == select-1).blockFirst().getT2();
		rescheduleReservation(selection, null);
	}
	
	private void rescheduleReservation(Reservation selection, String destination) {
		boolean isValid = false;
		Reservation update = new Reservation();
		while (!isValid) {
			if (selection.getType().equals(ReservationType.FLIGHT) 
					&& loggedUser.getType().equals(UserType.VACATIONER)){
				try {
					Flux<Flight> flights = us.getFlights(destination);
	
					Flux<Tuple2<Long, Flight>> flightsOrdered = flights.index();
					flightsOrdered.subscribe(f -> {
						System.out.println((f.getT1() + 1) + ": Buy a seat on an " + f.getT2().getAirline() + " flight for "
								+ f.getT2().getTicketPrice());
					});
					Long choiceIndex = Long.parseLong(scan.nextLine().trim());
					Flight choice = flightsOrdered.filter(t -> t.getT1().equals(choiceIndex - 1)).blockFirst().getT2();
					if (choice.getId().equals(selection.getReservedId())) {
						System.out.println("That is the same flight. Please choose a different flight.");
					} else {
						update.setReservedId(choice.getId());
						isValid = true;
					}
				} catch (Exception e) {
					System.out.println("Error with your selection. Please try again.");
				}
						
			} else {
				System.out.println("Please enter a new start date for this reservation (YYYY-MM-DD): ");
				String date = scan.nextLine().trim();
				System.out.println("Please enter a new start time for this reservation (HH:MM): ");
				String time = scan.nextLine().trim();
				String duration = "0";
				if (!selection.getType().equals(ReservationType.FLIGHT)) {
					System.out.println("Please enter how many days the reservation will be for: ");
					duration = scan.nextLine().trim();
				}
				
				try {
					LocalDate localDate = LocalDate.parse(date);
					LocalTime localTime = LocalTime.parse(time);
					LocalDateTime newStartTime = LocalDateTime.of(localDate, localTime);
					int newDuration = Integer.parseInt(duration);
					
					if (newDuration <= 0 && !selection.getType().equals(ReservationType.FLIGHT)) {
						throw new Exception();
					}
					update.setStartTime(newStartTime);
					update.setDuration(newDuration);
					isValid = true;
					
					
				} catch (Exception e) {
					System.out.println("Date, time and/or duration was invalid. Please try again.");
				}
			}
		
		}
		us.rescheduleReservation(update, selection.getId()).subscribe();

	}

	private void doVacationMenu() {
		// Print available user vacations with status assignment based on start time
		// If vacation time has started, then allow user checkout
		while (true) {
			switch (doVacationMenuInput()) {
			case "1": // View vacations
				getVacations();
				break;
			case "2": // View reservations for vacation
				viewResForVacationMenu();
				break;
			case "3": // Quit
				return;
			default:
				System.out.println("Invalid input. Try again");
				continue;
			}

		}

	}

	private String doVacationMenuInput() {
		System.out.println("What would you like to do?");
		System.out.println("1: View all entered vacations");
		System.out.println("2: View reservations for a vacation");
		System.out.println("3: Quit");

		return scan.nextLine().trim();
	}

	private void viewResForVacationMenu() {
		if (vaclist == null) {
			vaclist = new ArrayList<>();
			us.getUsersVacations(loggedUser, vaclist);
		}

		if (vaclist.size() == 0) {
			System.out.println("No vacations available");
			return;
		}

		System.out.println("Which vacation would you like to update?");
		for (int i = 0; i < vaclist.size(); i++) {
			System.out.println(i + 1 + ": Vacation ID: " + vaclist.get(i).getId() + "\n\tCost: " + vaclist.get(i).getTotal()
					+ "\n\tDate: " + vaclist.get(i).getStartTime() + "\n\tStatus: "
					+ (vaclist.get(i).getEndTime().isBefore(LocalDateTime.now()) ? "No longer available\n" : "Open\n"));
		}

		// Leave menu option
		System.out.println(vaclist.size() + 1 + ": Quit");

		int selection = getUserInput();

		if (selection < 1 || selection > vaclist.size() + 1) {
			System.out.println("ERROR: Invalid input entered");
			return;
		}

		if (selection == vaclist.size() + 1) // Quit option selected
			return;

		if (vaclist.get(selection - 1).getReservations().size() == 0) {
			System.out.println("The vacation you selected does not have any associated reservations");
			System.out.println("Create reservations with this vacation ID or try another\n\n");
			return;
		}
		
		if(selection == vaclist.size() + 1) // Quit menu
			loginMenuInput();
		else
			modifyReservationsMenuInput(vaclist.get(selection - 1).getReservations());
	}

	private void modifyReservationsMenuInput(List<Reservation> rlist) {

		if (rlist.size() == 0) {
			System.out.println("You do not have any reservations booked");
			return;
		}

		// Print menu for reservations option in vacation
		System.out.println("Which reservation would you like to change?");
		for (int i = 0; i < rlist.size(); i++) {
			System.out.println(i + 1 + ": Reservation ID: " + rlist.get(i).getId() + "\n\tName: "
					+ rlist.get(i).getReservedName() + "\n\tType: " + rlist.get(i).getType()
					+ "\n\tStatus: "
					+ rlist.get(i).getStatus());
		}

		System.out.println(rlist.size() + 1 + ": Quit");

		int selection = getUserInput();
		
		if(selection == rlist.size()+1)	// Leave menu selected
			loginMenuInput();
		
		else 
			modifyReservationsMenu(rlist.get(selection - 1));

	}

	private void changeReservationStatus(Reservation res, String status) {
		if (ReservationStatus.valueOf(status) == null) {
			System.out.println("Invalid status entered");
			return;
		}

		res.setStatus(ReservationStatus.valueOf(status));
		us.updateReservation(res);
	}

	private void modifyReservationsMenu(Reservation res) {
		System.out.println("What would you like to do?");
		int selection = 0;
		if (res.getType() == ReservationType.FLIGHT) {
			
			if (loggedUser.getType() == UserType.FLIGHT_STAFF) {	// Menu for staff
				System.out.println("1: Confirm flight booking");
				System.out.println("2: Close flight booking");
				System.out.println("3: Cancel flight booking");
				System.out.println("0: Return to previous menu");
				selection = getUserInput();
				
				if (selection == 1) {
					System.out.println("Staff confirmed reservation for: " + res.getReservedName());
					changeReservationStatus(res, "CONFIRMED");
					return;
				}
				
				if (selection == 2) {
					System.out.println("Staff closed reservation for " + res.getReservedName());					
					changeReservationStatus(res, "CLOSED");
					return;
				}
				
				if(selection == 3) {
					System.out.println("Staff cancelled reservation for " + res.getReservedName());
					changeReservationStatus(res, "CANCELLED");
					return;
				}
				
				if (selection < 0 || selection > 3)
					System.out.println("Invalid input. Returning to previous menu");
					
				return;
				

			}
			// Cancel option only for vacationers
			System.out.println("1: Cancel flight booking");
			System.out.println("0: Return to previous menu");
			selection = getUserInput();

			if (selection == 1) {
				// Cancel flight if start time isn't past
				if (!ReservationStatus.AWAITING.equals(res.getStatus())) {
					System.out.println("This flight cannot be cancelled anymore.");
					return;
				}

				changeReservationStatus(res, "CANCELLED");
				return;
			}

			else {
				if (selection < 0 || selection > 2)
					System.out.println("Invalid input. Returning to previous menu");

				return;
			}

		} else {

			if (res.getType() == ReservationType.CAR) {
				if(loggedUser.getType() == UserType.CAR_STAFF) {
					System.out.println("1: Confirm rental car reservation");
					System.out.println("2: Close rental car reservation");
					System.out.println("3: Cancel rental car reservation");
					System.out.println("0: Return to previous menu");
					
					selection = getUserInput();

					if (selection == 1) {
						System.out.println("Staff confirmed reservation for: " + res.getReservedName());
						changeReservationStatus(res, "CONFIRMED");
						return;
					}
					
					if (selection == 2) {
						System.out.println("Staff closed reservation for " + res.getReservedName());
						changeReservationStatus(res, "CLOSED");
						return;
					}

					if (selection == 3) {
						System.out.println("Staff cancelled reservation for " + res.getReservedName());
						changeReservationStatus(res, "CANCELLED");
						return;
					}

					else {
						if (selection < 0 || selection > 3)
							System.out.println("Invalid input. Returning to previous menu");

						return;
					}
				}
				
				// Vacationer only options
				System.out.println("1: Return rental car");
				System.out.println("2: Cancel rental reservation");
				System.out.println("0: Return to previous menu");

			}

			if (res.getType() == ReservationType.HOTEL) {
				if(loggedUser.getType() == UserType.HOTEL_STAFF) {
					System.out.println("1: Confirm Hotel reservation");
					System.out.println("2: Close Hotel reservation");
					System.out.println("3: Cancel Hotel reservation");
					System.out.println("0: Return to previous menu");					
					
					selection = getUserInput();

					if (selection == 1) {
						System.out.println("Staff confirmed reservation for: " + res.getReservedName());
						changeReservationStatus(res, "CONFIRMED");
						return;
					}
					
					if (selection == 2) {
						System.out.println("Staff closed reservation for " + res.getReservedName());
						changeReservationStatus(res, "CLOSED");
						return;
					}

					if (selection == 3) {
						System.out.println("Staff cancelled reservation for " + res.getReservedName());
						changeReservationStatus(res, "CANCELLED");
						return;
					}

					else {
						if (selection < 0 || selection > 3)
							System.out.println("Invalid input. Returning to previous menu");

						return;
					}
				}
				
				System.out.println("1: Check out of Hotel");
				System.out.println("2: Cancel Hotel reservation");
				System.out.println("0: Return to previous menu");

			}

			selection = getUserInput();

			if (selection == 1) {
				if (!ReservationStatus.CONFIRMED.equals(res.getStatus())) {
					// Attempting to complete reservation before it starts -> ERROR
					System.out.println("This reservation cannot be returned.");
					return;
				}

				System.out.println("Thanks for completing your reservation for " + res.getReservedName());
				changeReservationStatus(res, "CLOSED");
			}

			if (selection == 2) {
				// Cancel reservation if it hasn't started, otherwise close it

				if (!ReservationStatus.AWAITING.equals(res.getStatus())) {
					// Attempting to close a reservation that already started - no refund for you
					System.out.println("This reservation cannot be cancelled.");
					return;
				}

				System.out.println("Canceling reservation for " + res.getReservedName());
				changeReservationStatus(res, "CANCELLED");
			}

			else {
				if (selection < 0 || selection > 2)
					System.out.println("Invalid input. Returning to previous menu");

				return;
			}
		}

	}

	private void getVacations() {
		if (loggedUser.getVacations() == null || loggedUser.getVacations().size() == 0) {
			System.out.println("No vacations booked");
			return;
		}

		vaclist = new ArrayList<>();
		us.getUsersVacations(loggedUser, vaclist).blockLast();

		System.out.println("Vacation list retrieved from getUserVacations: " + vaclist);
	}

	private void logout() {
		// Logs the user out and returns them to the start menu
		us.logout().subscribe();
		loggedUser = null;

	}

	private boolean deleteAccount() {
		// Asks for the user's confirmation, then deletes their account
		System.out.println("Are you sure you want to delete your account? (y/n)");
		if (scan.nextLine().trim().equals("y")) {
			us.deleteAccount(loggedUser);
			us.logout();
			System.out.println("Account Deleted");
			return true;
		}
		return false;
	}

	private int getUserInput() {
		String input = scan.nextLine().trim();
		int selection = 0;
		try {
			selection = Integer.parseInt(input);
		} catch (Exception e) {
			System.out.println("ERROR: Invalid input entered");
			return 0;
		}

		return selection;

	}

}


//	1: Register Done
//	2: Login Done
//		(IF STAFF) Change reservation status Stephen
//			(Displays all reservations)
//			Select one to view
//				Confirm
//		1: Create Vacation Michael
//		2: Edit Vacation
//			(Displays all reservations made)
//			1: Reserve a car Done
//			2: Book a hotel Done
//			3: Book a flight Done
//			4: Add an activity Elizabeth
//				Select a location
//					(Displays all Activities at that location)
//					Select an activity 
//			5: edit an existing reservation 
//				(Displays all reservations on that vacation)
//				1: Reschedule the reservation Michael
//				2: Cancel the reservation Stephen
//		3. Complete Reservation Stephen
//			1. Checkout of hotel
//			2. Return Car
//		4: Logout Done
//		5: Delete account Kyle
//	3: Quit


