package ui;

import model.UserData;

import java.net.HttpURLConnection;

public class UserInputHandler {
	public static UserData getUserRegistrationData() {
		System.out.print("Enter username: ");
		String username = Repl.scanner.nextLine();
		System.out.print("Enter password: ");
		String password = Repl.scanner.nextLine();
		System.out.print("Enter email: ");
		String email = Repl.scanner.nextLine();

		return new UserData(username, password, email);
	}

	public static UserData getUserLoginData() {
		System.out.print("Enter username: ");
		String username = Repl.scanner.nextLine();
		System.out.print("Enter password: ");
		String password = Repl.scanner.nextLine();

		return new UserData(username, password);
	}
}
