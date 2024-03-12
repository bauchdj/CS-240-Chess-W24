package ui;

import model.UserData;

import static ui.HttpConnection.*;
import static ui.UserInputHandler.*;

import com.google.gson.JsonObject;
import java.net.HttpURLConnection;

public class PreLoginUI extends Repl {
	public PreLoginUI(Application app) {
		super(app);
	}

	@Override
	protected void displayPrompt() {
		System.out.println("1. Register");
		System.out.println("2. Login");
		System.out.println("Type 'quit' to exit at any time.");
		System.out.print("Enter your choice: ");
	}

	@Override
	protected void processInput(String input) {
		switch (input) {
			case "1":
				register();
				break;
			case "2":
				login();
				break;
			default:
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