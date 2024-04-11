package handlers;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
			(joinPlayer.getPlayerColor() == ChessGame.TeamColor.BLACK &&
				gameData.getBlackUsername() != null &&
				gameData.getBlackUsername().equals(username))) {

			ChessGame game = gameData.getGame();

			// Send LOAD_GAME message to the root client
			LoadGame loadGame = new LoadGame(game);
			sendMessage(gson.toJson(loadGame));

			addSession(gameID, session);

			// Send Notification message to all other clients
			Notification notification = new Notification("New Join! User: " + username + ", Color: " + joinPlayer.getPlayerColor());
			sendNotification(gameID, notification);
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
		sendNotification(gameID, notification);
	}

	private void handleMakeMove(MakeMove makeMove) {
		int gameID = makeMove.getGameID();
		ChessMove move = makeMove.getMove();

		// Retrieve the game from the database using gameDAO.getGame(makeMove.getGameId())
		GameData gameData = gameDAO.getGame(gameID);
		ChessGame game = gameData.getGame();

		ChessGame.TeamColor teamColor = makeMove.getPlayerColor();
		// Tests don't send teamColor :(
		if (teamColor == null) {
			String username = authDAO.getAuth(new AuthData(makeMove.getAuthToken())).getUsername();

			// Observer not player...
			if (!gameDAO.userIsPlayer(username, gameID)) {
				sendErrorMessage("Invalid username: " + username);
				return;
			}

			teamColor = gameDAO.getUserColorInGame(username, gameID);
		}

		ChessGame.TeamColor teamTurn = game.getTeamTurn();

		// If you color doesn't match turn color, not your turn
		if (teamColor != teamTurn) {
			sendErrorMessage("Invalid: not your turn. Current turn: " + teamTurn);
			return;
		}

		try {
			// Verify the validity of the move using game.isValidMove(makeMove.getMove())
			//   - Update the game state using game.makeMove(makeMove.getMove())
			game.makeMove(move);
		} catch (InvalidMoveException e) {
			// If the move is invalid:
			//   - Send Error message to the root client
			String str = (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != teamTurn) ?
				", your trying to move the other team's piece silly" :
				", you can't move there...";
			sendErrorMessage("Invalid move: " + move + str);
			return;
		}

		//   - Check if the game is in check, checkmate, or stalemate
		game.isInCheck(teamTurn);
		game.isInCheckmate(teamTurn);
		game.isInStalemate(teamTurn);

		//   - Update the game in the database using gameDAO.updateGame(game)
		gameDAO.updateGame(gameID, new GameData(gameID, gameData.getGameName(), game));

		//   - Send LOAD_GAME message to all clients in the game
		sendLoadGameAll(gameID, game);

		//   - Broadcast Notification message to all other clients in the game
		Notification notification = new Notification("Move: " + move);
		sendNotification(gameID, notification);
	}

	private void handleLeave(Leave leave) {
		// Retrieve the game from the database using gameDAO.getGame(leave.getGameId())
		// Retrieve the user information from the database using userDAO.getUser(leave.getAuthToken())
		// If the user is a player:
		//   - Update the game state to remove the player (game.removePlayer(user))
		//   - Update the game in the database using gameDAO.updateGame(game)
		// If the user is an observer:
		//   - Update the game state to remove the observer (game.removeObserver(user))
		// Broadcast Notification message to all other clients in the game

		int gameID = leave.getGameID();

		removeSession(gameID, session);

		// Update the game in the database using gameDAO.updateGame(game)
		String username = authDAO.getAuth(new AuthData(leave.getAuthToken())).getUsername();
		gameDAO.removeUserFromGame(username, gameID);

		// Broadcast Notification message to all clients in the game
		Notification notification = new Notification("Leaving! User: " + username);
		sendNotification(gameID, notification);
	}

	private void handleResign(Resign resign) {
		int gameID = resign.getGameID();

		// Broadcast Notification message to all clients in the game
		String username = authDAO.getAuth(new AuthData(resign.getAuthToken())).getUsername();

		// Observer not player...
		if (!gameDAO.userIsPlayer(username, gameID)) {
			sendErrorMessage("Invalid username: " + username);
			return;
		}

		Notification notification = new Notification("Resignation! User: " + username);
		sendNotificationAll(gameID, notification);

		removeSession(gameID, session);

		// Update the game in the database using gameDAO.updateGame(game)
		gameDAO.removeUserFromGame(username, gameID);
	}

	private void sendNotification(int gameID, Notification notification) {
		String message = gson.toJson(notification);

		HashSet<Session> setSessions = mapGameIDToSessions.get(gameID);
		if (setSessions != null) {
			for (Session session : setSessions) {
				if (this.session != session) {
					sendMessage(session, message);
				}
			}
		}
	}

	private void sendNotificationAll(int gameID, Notification notification) {
		String message = gson.toJson(notification);

		HashSet<Session> setSessions = mapGameIDToSessions.get(gameID);
		if (setSessions != null) {
			for (Session session : setSessions) {
				sendMessage(session, message);
			}
		}
	}

	private void sendLoadGameAll(int gameID, ChessGame game) {
		LoadGame loadGame = new LoadGame(game);
		String message = gson.toJson(loadGame);

		HashSet<Session> setSessions = mapGameIDToSessions.get(gameID);
		for (Session session : setSessions) {
			sendMessage(session, message);
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
