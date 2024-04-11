package handlers;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashSet;

import static handlers.WebSocketHandler.mapGameIDToSessions;

public class WebSocketMessageHandler {
	private static final Gson gson = new Gson();
	private static AuthDAO authDAO = null;
	private static GameDAO gameDAO = null;
	private static UserDAO userDAO = null;

	private final Session session;

	public WebSocketMessageHandler(Session session) {
		this.session = session;
	}

	public static void setDAOs(AuthDAO auth, GameDAO game, UserDAO user) {
		authDAO = auth;
		gameDAO = game;
		userDAO = user;
	}

	public void handleMessage(String message) {
		System.out.println("Message received: " + message);

		UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

		if (!authDAO.authExists(new AuthData(command.getAuthToken()))) {
			sendErrorMessage("Invalid authToken");
			return;
		}
		if (!gameDAO.gameExist(command.getGameID())) {
			sendErrorMessage("Invalid gameID");
			return;
		}

		switch (command.getCommandType()) {
			case JOIN_PLAYER:
				handleJoinPlayer(gson.fromJson(message, JoinPlayer.class));
				break;
			case JOIN_OBSERVER:
				handleJoinObserver(gson.fromJson(message, JoinObserver.class));
				break;
			case MAKE_MOVE:
				handleMakeMove(gson.fromJson(message, MakeMove.class));
				break;
			case LEAVE:
				handleLeave(gson.fromJson(message, Leave.class));
				break;
			case RESIGN:
				handleResign(gson.fromJson(message, Resign.class));
				break;
			default:
				sendErrorMessage("Unknown command type");
		}
	}

	private void handleJoinPlayer(JoinPlayer joinPlayer) {
		int gameID = joinPlayer.getGameID();
		GameData gameData = gameDAO.getGame(gameID);

		String username = authDAO.getAuth(new AuthData(joinPlayer.getAuthToken())).getUsername();

		if ((joinPlayer.getPlayerColor() == ChessGame.TeamColor.WHITE &&
				gameData.getWhiteUsername() != null &&
				gameData.getWhiteUsername().equals(username)) ||
			joinPlayer.getPlayerColor() == ChessGame.TeamColor.BLACK &&
				gameData.getBlackUsername() != null &&
				gameData.getBlackUsername().equals(username)) {

			ChessGame game = gameData.getGame();

			// Send LOAD_GAME message to the root client
			LoadGame loadGame = new LoadGame(game);
			sendMessage(gson.toJson(loadGame));

			addSession(gameID, session);

			// Send Notification message to all other clients
			Notification notification = new Notification("New Join! User: " + username + ", Color: " + joinPlayer.getPlayerColor());
			sendNotification(gameID, gson.toJson(notification));
		} else {
			if ((joinPlayer.getPlayerColor() == ChessGame.TeamColor.WHITE && gameData.getWhiteUsername() != null) ||
				joinPlayer.getPlayerColor() == ChessGame.TeamColor.BLACK && gameData.getBlackUsername() != null)
					sendErrorMessage("Color already in use, GameID: " + gameID);
			else
				sendErrorMessage("Prior HTTP join request DNE, GameID: " + gameID);
		}
	}

	private void handleJoinObserver(JoinObserver joinObserver) {
		int gameID = joinObserver.getGameID();
		GameData gameData = gameDAO.getGame(gameID);
		ChessGame game = gameData.getGame();

		// Send LOAD_GAME message to the root client
		LoadGame loadGame = new LoadGame(game);
		sendMessage(gson.toJson(loadGame));

		addSession(gameID, session);

		// Send Notification message to all other clients
		String username = authDAO.getAuth(new AuthData(joinObserver.getAuthToken())).getUsername();
		Notification notification = new Notification("New Observer! User: " + username);
		sendNotification(gameID, gson.toJson(notification));
	}

	private void handleMakeMove(MakeMove makeMove) {
		// Handle make move logic
		// Verify the validity of the move
		// Update the game state and database
		// Send LOAD_GAME message to all clients
		// Send Notification message to all other clients
	}

	private void handleLeave(Leave leave) {
		// Handle leave logic
		// Update the game state and database if a player is leaving
		// Send Notification message to all other clients
	}

	private void handleResign(Resign resign) {
		int gameID = resign.getGameID();;

		removeSession(gameID, session);

		// Mark the game as over and update the database
		// Send Notification message to all clients
	}

	private void sendNotification(int gameID, String message) {
		HashSet<Session> setSessions = mapGameIDToSessions.get(gameID);
		for (Session session : setSessions) {
			if (this.session != session) {
				sendMessage(session, message);
			}
		}
	}

	private void sendErrorMessage(String errorMessage) {
		ServerMessageError serverMessageError = new ServerMessageError(errorMessage);
		sendMessage(gson.toJson(serverMessageError));
	}

	private void sendMessage(String message) {
		sendMessage(session, message);
	}
	private void sendMessage(Session session, String message) {
		try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addSession(int key, Session session) {
		HashSet<Session> sessions = mapGameIDToSessions.get(key);
		if (sessions == null) {
			sessions = new HashSet<>();
			mapGameIDToSessions.put(key, sessions);
		}
		sessions.add(session);
	}


	public void removeSession(int key, Session session) {
		HashSet<Session> sessions = mapGameIDToSessions.get(key);
		if (sessions != null) {
			sessions.remove(session);
			if (sessions.isEmpty()) {
				mapGameIDToSessions.remove(key);
			}
		}
	}
}
