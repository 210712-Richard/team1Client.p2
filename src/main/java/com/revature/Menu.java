package com.revature;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.beans.Reservation;
import com.revature.beans.ReservationStatus;
import com.revature.beans.ReservationType;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.beans.Vacation;
import com.revature.services.UserService;

@Component
public class Menu {
	@Autowired
	private UserService us;
	@Autowired
	private Scanner scan;
	
	private static User loggedUser;
	private static List<Vacation> vlist;


	public static void setLoggedUser(User u) {
		loggedUser = u;
	}

	public void start() {
		while(true) {
			switch(mainMenuInput()) {
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
		System.out.println("Vacationeer!\n"
				+ "1: Register for a new account\n"
				+ "2: Login\n"
				+ "3: Quit\n");
		return scan.nextLine().trim();
	}

	private void register() {
		// Takes input and registers user with a request based on that input
		
	}
	

	private void login() {
		// Takes input and logs in user with a request based on that input
		User u = new User();
		System.out.println("Enter your username");
		u.setUsername(scan.nextLine().trim());
		System.out.println("Enter your password");
		u.setPassword(scan.nextLine().trim());
		loggedUser = us.login(u)
		.map(user -> {
			System.out.println("Login Successful");
			return user;
		})
		.block();
		loginMenu();
//		while(loggedUser == null) {
//			try {
//				Thread.sleep(100);
//				System.out.println("waiting for login");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		loginMenu();
	}


	private void loginMenu() {
		if (loggedUser == null) {
			System.out.println("Incorrect login credentials");
			return;
		}
		while(true) {
			switch (loginMenuInput()) {
				case "1":
					createVacation();
					break;
				case "2":
					editVacationMenu();
					break;
				case "3":
					doVacationMenu();
					break;
				case "4":
					logout();
					return;
				case "5":
					deleteAccount();
					break;
				case "6":
					confirmReservationMenu();
					break;
			}
		}
	}

	private String loginMenuInput() {
		// Using the scanner, asks the user for one of the above 5 loginmenu choices
				System.out.println("Welcome, "+loggedUser.getUsername()+"\n"
						+ "1: Create a new Vacation\n"
						+ "2: Edit a vacation\n"
						+ "3: Go on a vacation!\n"
						+ "4: Logout\n"
						+ "5: Delete Account\n");
				if (!loggedUser.getType().equals(UserType.VACATIONER)) {
					System.out.println("6: confirm a reservation]n");
				}
				return scan.nextLine().trim();
	}

	private void createVacation() {
		// Takes input, creates vacation based on input, redirects to edit that vacation
		
	}
	
	private void editVacationMenu() {
		while(true) {
			switch(editVacationInput()) {
				case "1":
					addCar();
					break;
				case "2":
					addHotel();
					break;
				case "3":
					addFlight();
					break;
				case "4":
					addActivityMenu();
					break;
				case "5":
					rescheduleReservationMenu();
					break;
				case "6":
					return;
			}
		}
		
	}
	

	private String editVacationInput() {
		// Using the scanner, asks the user for one of the above 6 editvacationmenu choices
		return "";
	}

	private void addCar() {
		// Gets user input for which car to add
		
	}

	private void addHotel() {
		// Gets user input for which hotel to add
		
	}

	private void addFlight() {
		// Gets user input for which flight to add
		
	}

	private void addActivityMenu() {
		// Gets the usres's input for where to search for activities
		while(true) {
			String location = scan.nextLine();
			switch(location) {
				case "":
					return;
				default:
					addActivity(location);
			}
		}
	}

	private void addActivity(String location) {
		// COMPLEX
		// Shows the user all activities at the given location, 
		// and lets them choose one to add
		
	}

	private void rescheduleReservationMenu() {
		// VERY COMPLEX
		// With one choice for each reservation in the selected vacation, 
		// each choice with allow the user to edit its corresponding reservation
		
	}


	private void doVacationMenu() {
		// Print available user vacations with status assignment based on start time
		// If vacation time has started, then allow user checkout
		while(true) {
			switch(doVacationMenuInput()) {
			case "1":	// View vacations
				getVacations();
				break;
			case "2":	// View reservations for vacation
				viewResForVacationMenu();
				
				// If result is not null	
				break;			
			case "3":	// Quit
				return;
			default: System.out.println("Invalid input. Try again");
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
		if(vlist == null) {
			vlist = new ArrayList<>();
			us.getUsersVacations(loggedUser, vlist);
		}
		
		if(vlist.size() == 0) {
			System.out.println("No vacations available");
			return;
		}
		
		System.out.println("Which vacation would you like to update?");
		for(int i = 0; i < vlist.size(); i++) {
			System.out.println(i+1 + ": Vacation ID: " + vlist.get(i).getId() + 
					"\n\tCost: " + vlist.get(i).getTotal() +
					"\n\tDate: " + vlist.get(i).getStartTime() +
					"\n\tStatus: " + (vlist.get(i).getEndTime().isBefore(LocalDateTime.now()) 
							? "No longer available\n" : "Open\n"));
		}
		
		// Leave menu option
		System.out.println(vlist.size()+1 + ": Quit");	
		
		int selection = getUserInput();
		
		if(selection < 1 || selection > vlist.size()+1) {
			System.out.println("ERROR: Invalid input entered");
			return;
		}
		
		if(selection == vlist.size()+1)	// Quit option selected
			return;
		
		if(vlist.get(selection-1).getReservations().size() == 0) {
			System.out.println("The vacation you selected does not have any associated reservations");
			System.out.println("Create reservations with this vacation ID or try another\n\n");
			return;
		}
		
		modifyResForVacationMenuInput(vlist.get(selection-1));
	}

	private void modifyResForVacationMenuInput(Vacation v) {

		if(v.getReservations().size() == 0) {
			System.out.println("You do not have any reservations booked on this vacation");
			return;
		}

		// Print menu for reservations option in vacation
		System.out.println("Which reservation would you like to change?");
		for(int i = 0; i < v.getReservations().size(); i++) {
			System.out.println(i+1 + ": Reservation ID: " + v.getReservations().get(i).getId() + 
					"\n\tName: " + v.getReservations().get(i).getReservedName() +
					"\n\tType: " + v.getReservations().get(i).getType() +
					"\n\tTime: " + v.getReservations().get(i).getStarttime() +
					"\n\tStatus: " + v.getReservations().get(i).getStatus());
		}
		
		// Leave menu option
		System.out.println(v.getReservations().size()+1 + ": Quit");	
		
		int selection = getUserInput();
		modifyResForVacationMenu(v.getReservations().get(selection-1));

	}
	
	private void changeReservationStatus(Reservation res, String status) {
		if(ReservationStatus.valueOf(status) == null) {
			System.out.println("Invalid status entered");
			return;
		}
		System.out.println("Updating Reservation: " + res);
		res.setStatus(ReservationStatus.valueOf(status));
		us.updateReservation(res);
		System.out.println("Updated Reservation: " + res);
	}

	private void modifyResForVacationMenu(Reservation res) {
		System.out.println("What would you like to do?");
		int selection = 0;
		if(res.getType() == ReservationType.FLIGHT) {
			// Cancel option only
			System.out.println("1: Cancel flight booking");
			System.out.println("2: Return to previous menu");
			selection = getUserInput();
			
			if(selection == 1) {
				// Cancel flight if start time isn't past
				if(res.getStarttime().isBefore(LocalDateTime.now())) {
					System.out.println("Flight booking has expired, closing reservation");
					changeReservationStatus(res, "CLOSED");
					return;
				}
				
				changeReservationStatus(res, "CANCELLED");
			}
			
			else {
				if(selection <= 0 || selection > 2)
					System.out.println("Invalid input. Returning to previous menu");
				
				return;
			}

		}
		
		if(res.getType() == ReservationType.CAR) {
			System.out.println("1: Return rental car");
			System.out.println("2: Cancel rental reservation");
			System.out.println("3: Return to previous menu");
			
//			if(selection == 1) {
//				if(res.getStarttime().isBefore(LocalDateTime.now())) {
//					// Attempting to return a car you didn't pick up -> ERROR
//					System.out.println("This rental has not been picked up yet");
//					return;
//				}
//				
//				System.out.println("Thanks for returning the " + res.getReservedName() +" rental car.");
//				changeReservationStatus(res, "Closed");
//			}
//			
//			if(selection == 2) {
//				// Cancel rental car reservation if reservation hasn't started, otherwise close it
//				if(res.getStarttime().isAfter(LocalDateTime.now())) {
//					// Attempting to close a reservation that already started - no refund for you
//					System.out.println("This reservation already started, No refund will be issued for cancellation");
//					changeReservationStatus(res, "Closed");
//					return;
//				}
//				
//				System.out.println("Canceling reservation for " + res.getReservedName() + " rental");
//				changeReservationStatus(res, "Cancel");
//			}
//			
//			else {
//				if(selection <= 0 || selection > 3)
//					System.out.println("Invalid input. Returning to previous menu");
//				
//				return;
//			}

		}
		
		if(res.getType() == ReservationType.HOTEL) {
			System.out.println("1: Check out of Hotel");
			System.out.println("2: Cancel Hotel reservation");
			System.out.println("3: Return to previous menu");

		}
		
		selection = getUserInput();
		
		if(selection == 1) {
			if(res.getStarttime().isAfter(LocalDateTime.now())) {
				// Attempting to complete reservation before it starts -> ERROR
				System.out.println("This reservation hasn't started yet");
				return;
			}
			
			System.out.println("Thanks for completing your reservation for " + res.getReservedName());
			changeReservationStatus(res, "CLOSED");
		}
		
		if(selection == 2) {
			// Cancel reservation if it hasn't started, otherwise close it
			
			if(res.getStarttime().isBefore(LocalDateTime.now())) {
				// Attempting to close a reservation that already started - no refund for you
				System.out.println("This reservation already started, No refund will be issued for cancellation");
				changeReservationStatus(res, "CLOSED");
				return;
			}
			
			System.out.println("Canceling reservation for " + res.getReservedName());
			changeReservationStatus(res, "CANCELLED");
		}
		
		else {
			if(selection <= 0 || selection > 3)
				System.out.println("Invalid input. Returning to previous menu");
			
			return;
		}

	}

	private void getVacations() {
		if(loggedUser.getVacations() == null || loggedUser.getVacations().size() == 0) {
			System.out.println("No vacations booked");
			return;
		}
		
		vlist = new ArrayList<>();
		us.getUsersVacations(loggedUser, vlist).blockLast();
		
		System.out.println("Vacation list retrieved from getUserVacations: " + vlist);
	}

	private void logout() {
		// Logs the user out and returns them to the start menu
		us.logout().subscribe();
		loggedUser = null;		
		
	}
	
	private void deleteAccount() {
		// Asks for the user's confirmation, then deletes their account
		
	}

	private void confirmReservationMenu() {
		// COMPLEX
		// Displays all reservations to the staff user, giving them
		// the option to view each one, then confirm it
		
	}
	
	private int getUserInput() {
		String input = scan.nextLine().trim();
		int selection = 0;
		try {
			selection = Integer.parseInt(input);
		}
		catch(Exception e) {
			System.out.println("ERROR: Invalid input entered");
			return 0;
		}
		
		return selection;

	}

//	1: Register
//	2: Login
//		(IF STAFF) Confirm reservations
//			(Displays all reservations)
//			Select one to view
//				Confirm
//		1: Create Vacation
//		2: Edit Vacation
//			(Displays all reservations made)
//			1: Reserve a car
//			2: Book a hotel
//			3: Book a flight
//			4: Add an activity
//				Select a location
//					(Displays all Activities at that location)
//					Select an activity
//			5: edit an existing reservation
//				(Displays all reservations on that vacation)
//				1: Reschedule the reservation
//				2: Cancel the reservation
//		3: Go on Vacation
//			1: Attend flight
//			2: Check out of hotel
//			3: return car
//		4: Logout
//		5: Delete account
//	3: Quit
}
