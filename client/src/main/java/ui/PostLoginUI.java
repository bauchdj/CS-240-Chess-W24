package ui;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostLoginUI extends Repl {
	private static final String BASE_URL = "http://localhost:4567";
	private static final Gson gson = new Gson();

	public PostLoginUI(Application app) {
		super(app);
	}

	@Override
	protected void displayPrompt() {
		System.out.println("1. List Games");
		System.out.println("2. Create Game");
		System.out.println("3. Join Game");
		System.out.println("4. Observe Game");
		System.out.println("5. Logout");
		System.out.print("Enter your choice: ");
	}

	@Override
	protected void processInput(String input) {
		switch (input) {
			case "1":
				listGames();
				break;
			case "2":
				createGame();
				break;
			case "3":
				joinGame();
				break;
			case "4":
				observeGame();
				break;
			case "5":
				logout();
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
		}
	}

	private void listGames() {
		try {
			HttpURLConnection connection = createGetConnection("/game");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String response = readResponse(connection);
				// Process the response and display the list of games
				System.out.println("List of games:");
				System.out.println(response);
			} else {
				System.out.println("Failed to retrieve the list of games.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createGame() {
		System.out.print("Enter game name: ");
		String gameName = scanner.nextLine();

		try {
			HttpURLConnection connection = createPostConnection("/game");
			String requestBody = gson.toJson(new CreateGameRequest(gameName));
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Game created successfully!");
			} else {
				System.out.println("Failed to create the game.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void joinGame() {
		System.out.print("Enter game ID: ");
		int gameId = Integer.parseInt(scanner.nextLine());
		System.out.print("Enter client color: ");
		String clientColor = scanner.nextLine();

		try {
			HttpURLConnection connection = createPutConnection("/game");
			String requestBody = gson.toJson(new JoinGameRequest(gameId, clientColor));
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Joined the game successfully!");
				app.navigateToGamePlay();
			} else {
				System.out.println("Failed to join the game.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void observeGame() {
		System.out.print("Enter game ID: ");
		int gameId = Integer.parseInt(scanner.nextLine());

		try {
			HttpURLConnection connection = createPutConnection("/game");
			String requestBody = gson.toJson(new ObserveGameRequest(gameId));
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Observing the game now!");
				app.navigateToGamePlay();
			} else {
				System.out.println("Failed to observe the game.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logout() {
		try {
			HttpURLConnection connection = createDeleteConnection("/session");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Logged out successfully!");
				app.navigateToPreLogin();
			} else {
				System.out.println("Failed to logout.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HttpURLConnection createGetConnection(String endpoint) throws Exception {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", app.getAuthToken());
		return connection;
	}

	private HttpURLConnection createPostConnection(String endpoint) throws Exception {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", app.getAuthToken());
		connection.setDoOutput(true);
		return connection;
	}

	private HttpURLConnection createPutConnection(String endpoint) throws Exception {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", app.getAuthToken());
		connection.setDoOutput(true);
		return connection;
	}

	private HttpURLConnection createDeleteConnection(String endpoint) throws Exception {
		URL url = new URL(BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Authorization", app.getAuthToken());
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
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();
		return response.toString();
	}

	private static class CreateGameRequest {
		private String gameName;

		public CreateGameRequest(String gameName) {
			this.gameName = gameName;
		}
	}

	private static class JoinGameRequest {
		private int gameId;
		private String clientColor;

		public JoinGameRequest(int gameId, String clientColor) {
			this.gameId = gameId;
			this.clientColor = clientColor;
		}
	}

	private static class ObserveGameRequest {
		private int gameId;

		public ObserveGameRequest(int gameId) {
			this.gameId = gameId;
		}
	}
}
