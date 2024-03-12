package ui;

import model.UserData;

import static ui.HttpConnection.*;
import static ui.UserInputHandler.*;

import com.google.gson.JsonObject;

public class PreLoginUI extends Repl {
	private enum MenuOption {
		REGISTER(1, "Register"),
		LOGIN(2, "Login");

		private final int number;
		private final String description;

		MenuOption(int number, String description) {
			this.number = number;
			this.description = description;
		}

		public int getNumber() {
			return number;
		}

		public String getDescription() {
			return description;
		}
	}

	public PreLoginUI(ServerFacade app) {
		super(app);
	}

	@Override
	protected void displayPrompt() {
		for (MenuOption option : MenuOption.values()) {
			System.out.println(option.getNumber() + ". " + option.getDescription());
		}
	}

	@Override
	protected void displayHelp() {
		System.out.println("Pre Login Help:");
		System.out.println("- Register: Create a new account");
		System.out.println("- Login: Log in to an existing account");
	}

	@Override
	protected void processInput(String input) {
		try {
			int choice = Integer.parseInt(input);
			MenuOption selectedOption = MenuOption.values()[choice - 1];
			switch (selectedOption) {
				case REGISTER:
					register();
					break;
				case LOGIN:
					login();
					break;
			}
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid choice. Please try again.");
		}
	}

	private void register() {
		UserData userData = getUserRegistrationData();
		String requestBody = gson.toJson(userData);

		sendPostRequest("/user", requestBody, (response) -> {
			System.out.println("Registration successful!");
		}, () -> {
			System.out.println("Registration failed. Please try again.");
		});
	}

	private void login() {
		UserData userData = getUserLoginData();
		String requestBody = gson.toJson(userData);

		sendPostRequest("/session", requestBody, (response) -> {
			String authToken = extractAuthToken(response);
			app.storeAuthToken(authToken);

			setAuthToken(authToken);
			navigate();
			app.navigateToPostLogin();
		}, () -> {
			System.out.println("Login failed. Please try again.");
		});
	}

	private String extractAuthToken(String response) {
		return gson.fromJson(response, JsonObject.class)
				.get("authToken").getAsString();
	}
}