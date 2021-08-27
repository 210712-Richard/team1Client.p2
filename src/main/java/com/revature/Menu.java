package com.revature;

import java.time.LocalDate;
import java.util.Scanner;

import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class Menu {
	private UserService us = new UserService();
	private Scanner scan = SingletonScanner.getScanner().getScan();
	private static User loggedUser;

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
				+ "3: Quit");
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
		while (birthday==null) {
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
			switch(scan.nextLine().trim()) {
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
		us.login(u);
		while(loggedUser == null) {
			try {
				Thread.sleep(100);
				System.out.println("waiting for login");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		loginMenu();
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
		while(true) {
			switch(doVacationMenuInput()) {
				case "1":
					attendFlightMenu();
					break;
				case "2":
					checkOutHotelMenu();
					break;
				case "3":
					returnCarMenu();
					break;
				case "4":
					return;
			}
					
		}
		
	}
	

	private String doVacationMenuInput() {
		// Using the scanner, asks the user for one of the above 4 dovacationmenu choices
		return "";
	}

	private void attendFlightMenu() {
		// COMPLEX
		// With one choice for each flight in the selected reservation, 
		// each choice with allow the user to fly its corresponding flight			
	}

	private void checkOutHotelMenu() {
		// COMPLEX
		// With one choice for each hotel in the selected reservation, 
		// each choice with allow the user to check out of its corresponding hotel	
	}

	private void returnCarMenu() {
		// COMPLEX
		// With one choice for each car in the selected reservation, 
		// each choice with allow the user to return its corresponding car		
	}

	private void logout() {
		// Logs the user out and returns them to the start menu
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
