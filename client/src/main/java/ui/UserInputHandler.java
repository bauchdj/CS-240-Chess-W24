package ui;

import model.*;

import static ui.Repl.scanner;

public class UserInputHandler {
	public static UserData getUserRegistrationData() {
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();
		System.out.print("Enter email: ");
		String email = scanner.nextLine();

		return new UserData(username, password, email);
	}

	public static UserData getUserLoginData() {
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();

		return new UserData(username, password);
	}

	public static String getUserInput(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}

	public static int getUserInputInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine();
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter an integer.");
			}
		}
	}
}
