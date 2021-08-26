package com.revature;

import java.util.Scanner;

import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class Menu {
	private UserService us = new UserService();
	private Scanner scan = SingletonScanner.getScanner().getScan();
	
	public void start() {
		mainLoop : while(true) {
//			1: Register
//			2: Login
//				(IF STAFF) Confirm reservations
//					(Displays all reservations)
//					Select one to view
//						Confirm
//				1: Create Vacation
//				2: Edit Vacation
//					(Displays all reservations made)
//					1: Reserve a car
//					2: Book a hotel
//					3: Book a flight
//					4: Add an activity
//						(Displays all Locations where there are activities)
//						Select a location
//							(Displays all Activities at that location)
//							Select an activity
//					5,6,... edit existing reservations
//						1: Reschedule the reservation
//						2: Cancel the reservation
//				3: Go on Vacation
//					1: Attend flight
//					2: Check out of hotel
//					3: return car
//				4: Logout
//				5: Delete account
//			3: Quit
			
		}
	}
}
