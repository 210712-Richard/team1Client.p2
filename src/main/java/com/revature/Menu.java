package com.revature;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.services.UserService;

@Component
public class Menu {
	@Autowired
	private UserService us;
	@Autowired
	private Scanner scan;
	
	public void start() {
		while(true) {
			switch(mainMenuInput()) {
				case "1":
					register();
					break;
				case "2":
					loginMenu();
					break;
				case "3":
					return;	
			}
		}
	}

	private String mainMenuInput() {
		// Using the scanner, asks the user for one of the above 3 mainmenu choices
		return "";
	}

	private void register() {
		// Takes input and registers user with a request based on that input
		
	}

	private void loginMenu() {
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
		return "";
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
