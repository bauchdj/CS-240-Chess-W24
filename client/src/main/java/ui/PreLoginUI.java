package ui;

import model.UserData;
import static ui.HttpConnection.*;

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
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();
		System.out.print("Enter email: ");
		String email = scanner.nextLine();

		UserData userData = new UserData(username, password, email);
		String requestBody = gson.toJson(userData);

		try {
			HttpURLConnection connection = createPostConnection("/user");
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Registration successful!");
			} else {
				System.out.println("Registration failed. Please try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login() {
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();

		UserData userData = new UserData(username, password);
		String requestBody = gson.toJson(userData);

		try {
			HttpURLConnection connection = createPostConnection("/session");
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String response = readResponse(connection);

				String authToken = gson.fromJson(response, JsonObject.class)
						.get("authToken").getAsString();
				app.storeAuthToken(authToken);

				navigate();
				app.navigateToPostLogin();
			} else {
				System.out.println("Login failed. Please try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}