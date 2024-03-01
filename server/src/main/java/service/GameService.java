package service;

import java.util.HashSet;
//import java.util.UUID;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;

public class GameService {
	private final GameDAO gameDAO;
	private final AuthDAO authDAO;
	private int gameID;

	public GameService(GameDAO gameDAO, AuthDAO authDAO) {
		this.gameDAO = gameDAO;
		this.authDAO = authDAO;
		this.gameID = 1;
	}

	public GameID createGame(AuthData authData, String gameName) {
		if (!this.authDAO.authExists(authData) || gameName.isEmpty()) return null;
		//int id = UUID.randomUUID().toString().hashCode() & 0x7FFFFFFF;
		int id = this.gameID;
		++this.gameID;
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(id, gameName, game);
		this.gameDAO.createGame(gameData);
		return new GameID(id);
	}

	public HashSet<GameData> listGames(AuthData authData) {
		if (!this.authDAO.authExists(authData)) return null;
		return this.gameDAO.listGames();
	}

	public String joinGame(AuthData reqAuthData, String clientColor, int gameID) {
		if (!this.authDAO.authExists(reqAuthData)) return "unauthorized";

		if (!this.gameDAO.gameExist(gameID)) return "game does not exist";

		AuthData dbAuthData = this.authDAO.getAuth(reqAuthData);
		String username = dbAuthData.getUsername();

		if (clientColor != null) {
			if (this.gameDAO.userExists(username, gameID, clientColor)) return "already taken";
			this.gameDAO.updateGame(username, gameID, clientColor);
		}

		return "success";
	}
}