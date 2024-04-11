package ui;

import chess.ChessGame;
import connection.*;

public class App {
	private enum State {
		PRE_LOGIN,
		POST_LOGIN,
		GAME_PLAY,
		EXIT
	}

	private State currentState;
	private PreLoginUI preLoginUI;
	private PostLoginUI postLoginUI;
	private GamePlayUI gamePlayUI;
	private String authToken;
	private WebSocketConnection connection;
	private int gameID = 0;
	private ChessGame.TeamColor color = null;

	public App(int port) {
		currentState = State.PRE_LOGIN;
		preLoginUI = new PreLoginUI(this);
		postLoginUI = new PostLoginUI(this);
		gamePlayUI = new GamePlayUI(this);
		HttpConnection.setBaseUrl("localhost", port);
		WebSocketConnection.setBaseUri("localhost", port, "/connect");
	}

	public void run() {
		while (currentState != State.EXIT) {
			switch (currentState) {
				case PRE_LOGIN:
					preLoginUI.run();
					break;
				case POST_LOGIN:
					postLoginUI.run();
					break;
				case GAME_PLAY:
					gamePlayUI.run();
					break;
			}
		}
		System.out.println("Exiting the application. Goodbye!");
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		HttpConnection.setAuthToken(authToken);
		this.authToken = authToken;
	}

	public WebSocketConnection getConnection() {
		return connection;
	}

	public void setConnection(WebSocketConnection connection) {
		this.connection = connection;
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public ChessGame.TeamColor getColor() {
		return color;
	}

	public void setColor(ChessGame.TeamColor color) {
		this.color = color;
	}

	public void navigateToPostLogin() {
		currentState = State.POST_LOGIN;
	}

	public void navigateToGamePlay() {
		currentState = State.GAME_PLAY;
	}

	public void navigateToPreLogin() {
		currentState = State.PRE_LOGIN;
	}
	public void exitApplication() {
		currentState = State.EXIT;
	}

	public static void main(String[] args) {
		App app = new App(4000);
		app.run();
	}
}
