package ui;

import static ui.HttpConnection.*;
import static ui.UserInputHandler.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class PostLoginUI extends Repl {
	private enum MenuOption {
		LIST_GAMES(1, "List Games"),
		CREATE_GAME(2, "Create Game"),
		JOIN_GAME(3, "Join Game"),
		OBSERVE_GAME(4, "Observe Game"),
		LOGOUT(5, "Logout");

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
	private List<JsonObject> gameList;
	public PostLoginUI(ServerFacade app) {
		super(app);
		gameList = new ArrayList<>();
	}

	@Override
	protected void displayPrompt() {
		System.out.println("Welcome! Let's Play Chess. Woohoo ;)");
		for (MenuOption option : MenuOption.values()) {
			System.out.println(option.getNumber() + ". " + option.getDescription());
		}
	}

	@Override
	protected void displayHelp() {
		System.out.println("Post Login Help:");
		System.out.println("- List Games: View available games");
		System.out.println("- Create Game: Create a new game");
		System.out.println("- Join Game: Join an existing game");
		System.out.println("- Observe Game: Observe an ongoing game");
		System.out.println("- Logout: Log out of the application");
	}

	@Override
	protected void processInput(String input) {
		try {
			int choice = Integer.parseInt(input);
			MenuOption selectedOption = MenuOption.values()[choice - 1];
			switch (selectedOption) {
				case LIST_GAMES:
					listGames();
					break;
				case CREATE_GAME:
					createGame();
					break;
				case JOIN_GAME:
					joinGame();
					break;
				case OBSERVE_GAME:
					observeGame();
					break;
				case LOGOUT:
					logout();
					break;
			}
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid choice. Please try again.");
		}
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

	private void listGames() {
		sendGetRequest("/game", (response) -> {
			System.out.println("Game List:");
			JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
			JsonArray gamesArray = jsonResponse.getAsJsonArray("games");

			gameList.clear();
			if (gamesArray.size() == 0) {
				System.out.println("No games available.");
			} else {
				for (int i = 0; i < gamesArray.size(); i++) {
					JsonObject gameObject = gamesArray.get(i).getAsJsonObject();
					gameList.add(gameObject);
					int gameId = gameObject.get("gameID").getAsInt();
					String gameName = gameObject.get("gameName").getAsString();
					System.out.println((i + 1) + ". " + gameName + " (ID: " + gameId + ")");
				}
			}
		}, () -> {
			System.out.println("Failed to retrieve game list. Please try again.");
		});
	}

	private void joinGame() {
		int gameId = selectGame();
		if (gameId != -1) {
			String clientColor = getUserInput("Enter your color (white or black): ");
			if (isValidColor(clientColor)) {
				sendPutGameRequest(gameId, clientColor.toLowerCase());
			} else {
				System.out.println("Invalid color. Please enter either 'white' or 'black'.");
			}
		}
	}

	private boolean isValidColor(String color) {
		String lowerCaseColor = color.toLowerCase();
		return lowerCaseColor.equals("white") || lowerCaseColor.equals("black");
	}

	private void observeGame() {
		int gameId = selectGame();
		if (gameId != -1) {
			sendPutGameRequest(gameId, null);
		}
	}

	private int selectGame() {
		listGames();

		if (!gameList.isEmpty()) {
			int selection = getUserInputInt("Enter the number of the game (0 to cancel): ");
			if (selection == 0) {
				return -1;
			} else if (selection >= 1 && selection <= gameList.size()) {
				JsonObject selectedGame = gameList.get(selection - 1);
				return selectedGame.get("gameID").getAsInt();
			} else {
				System.out.println("Invalid selection. Please try again.");
			}
		}

		return -1;
	}

	private void sendPutGameRequest(int gameId, String clientColor) {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameID", gameId);
		if (clientColor != null) {
			requestBody.addProperty("playerColor", clientColor);
		}

		sendPutRequest("/game", requestBody.toString(), (response) -> {
			System.out.println("Request successful!");
			navigate();
			app.navigateToGamePlay();
		}, () -> {
			System.out.println("Request failed. Please try again.");
		});
	}

	private void logout() {
		sendDeleteRequest("/session", (response) -> {
			System.out.println("Logged out successfully!");
			HttpConnection.setAuthToken(null);
			navigate();
			app.navigateToPreLogin();
		}, () -> {
			System.out.println("Failed to logout. Please try again.");
		});
	}
}