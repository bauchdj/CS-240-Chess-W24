package model;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
	private int gameID;
	private String whiteUsername;
	private String blackUsername;
	private String gameName;
	private ChessGame game; // Assuming ChessGame is an interface or class you have defined

	public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
		setGameID(gameID);
		setWhiteUsername(whiteUsername);
		setBlackUsername(blackUsername);
		setGameName(gameName);
		setGame(game);
	}

	public GameData(int gameID, String gameName, ChessGame game) {
		setGameID(gameID);
		setWhiteUsername(null);
		setBlackUsername(null);
		setGameName(gameName);
		setGame(game);
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public String getWhiteUsername() {
		return whiteUsername;
	}

	public void setWhiteUsername(String whiteUsername) {
		this.whiteUsername = whiteUsername;
	}

	public String getBlackUsername() {
		return blackUsername;
	}

	public void setBlackUsername(String blackUsername) {
		this.blackUsername = blackUsername;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public ChessGame getGame() {
		return game;
	}

	public void setGame(ChessGame game) {
		this.game = game;
	}

	@Override
	public String toString() {
		return "Game{" +
				"gameID=" + gameID +
				", whiteUsername='" + whiteUsername + '\'' +
				", blackUsername='" + blackUsername + '\'' +
				", gameName='" + gameName + '\'' +
				//", game=" + game +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameData gameData = (GameData) o;
		return gameID == gameData.gameID && Objects.equals(whiteUsername, gameData.whiteUsername) && Objects.equals(blackUsername, gameData.blackUsername) && Objects.equals(gameName, gameData.gameName) && Objects.equals(game, gameData.game);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
	}
}

