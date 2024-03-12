package ui;

import model.UserData;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static ui.Application.BASE_URL;

public class PreLoginUI extends Repl {
	private Application app;
	private static final Gson gson = new Gson();

	public PreLoginUI(Application app) {
		this.app = app;
	}

	@Override
	protected void displayPrompt() {
		System.out.println("1. Register");
		System.out.println("2. Login");
		System.out.print("Enter your choice: ");
	}

	@Override
	protected boolean shouldExit(String input) {
		return input.equalsIgnoreCase("quit");
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
			if (responseCode == HttpURLConnection.HTTP_CREATED) {
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
				// Handle the response, e.g., store the session token for further requests
				app.navigateToPostLogin();
			} else {
				System.out.println("Login failed. Please try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HttpURLConnection createPostConnection(String endpoint) throws Exception {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		return connection;
	}

	private void sendRequest(HttpURLConnection connection, String requestBody) throws Exception {
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(requestBody.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	private String readResponse(HttpURLConnection connection) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String response = reader.readLine();
		reader.close();
		return response;
	}
}