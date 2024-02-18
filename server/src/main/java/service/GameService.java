package service;

import java.util.List;
import java.util.UUID;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;

public class GameService {
	private final GameDAO gameDAO;
	private final AuthDAO authDAO;

	public GameService(GameDAO gameDAO, AuthDAO authDAO) {
		this.gameDAO = gameDAO;
		this.authDAO = authDAO;
	}

	public GameID createGame(AuthData authData, String gameName) {
		if (!this.authDAO.authExists(authData)) return null;
		int id = Integer.parseInt(UUID.randomUUID().toString());
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(id, gameName, game);
		this.gameDAO.createGame(gameData);
		return new GameID(id);
	}

	public List<GameData> listGames(AuthData authData) {
		if (!this.authDAO.authExists(authData)) return null;
		List<GameData> games = this.gameDAO.listGames();
		return games;
	}

	public boolean joinGame(AuthData reqAuthData, String clientColor, int gameID) {
		if (!this.authDAO.authExists(reqAuthData)) return false;
		AuthData dbAuthData = this.authDAO.getAuth(reqAuthData);
		String username = dbAuthData.getUsername();
		System.out.println(clientColor);
		this.gameDAO.updateGame(username, gameID, clientColor);
		return true;
	}
}