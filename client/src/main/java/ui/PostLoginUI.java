package ui;

import static ui.HttpConnection.*;
import static ui.UserInputHandler.*;

import com.google.gson.JsonObject;

public class PostLoginUI extends Repl {
	public PostLoginUI(Application app) {
		super(app);
	}

	@Override
	protected void displayPrompt() {
		System.out.println("Welcome to Chess Woohoo!");
		System.out.println("1. List Games");
		System.out.println("2. Create Game");
		System.out.println("3. Join Game");
		System.out.println("4. Observe Game");
		System.out.println("5. Logout");
		System.out.println("Type 'quit' to exit at any time.");
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
		sendGetRequest("/game", (response) -> {
			System.out.println("Game List:");
			System.out.println(response);
		}, () -> {
			System.out.println("Failed to retrieve game list. Please try again.");
		});
	}

	private void createGame() {
		String gameName = getUserInput("Enter game name: ");
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameName", gameName);

		sendPostRequest("/game", requestBody.toString(), (response) -> {
			System.out.println("Game created successfully!");
		}, () -> {
			System.out.println("Failed to create game. Please try again.");
		});
	}

	private void joinGame() {
		int gameId = getUserInputInt("Enter game ID: ");
		String clientColor = getUserInput("Enter client color: ");
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameId", gameId);
		requestBody.addProperty("clientColor", clientColor);

		sendPutRequest("/game", requestBody.toString(), (response) -> {
			System.out.println("Joined the game successfully!");
			app.navigateToGamePlay();
		}, () -> {
			System.out.println("Failed to join the game. Please try again.");
		});
	}

	private void observeGame() {
		int gameId = getUserInputInt("Enter game ID: ");
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameId", gameId);

		sendPutRequest("/game", requestBody.toString(), (response) -> {
			System.out.println("Observing the game now!");
			app.navigateToGamePlay();
		}, () -> {
			System.out.println("Failed to observe the game. Please try again.");
		});
	}

	private void logout() {
		sendDeleteRequest("/session", (response) -> {
			System.out.println("Logged out successfully!");
			app.navigateToPreLogin();
		}, () -> {
			System.out.println("Failed to logout. Please try again.");
		});
	}
}