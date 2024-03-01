package dataAccess;

import java.sql.SQLException;
import com.google.gson.Gson;

import chess.ChessGame;

import model.AuthData;
import model.GameData;
import model.UserData;


import java.util.HashSet;

public class MySQLDatabase implements DataAccess {
	private static final String usersTableStatement =
			"""
			CREATE TABLE IF NOT EXISTS users (
				username VARCHAR(32) NOT NULL UNIQUE PRIMARY KEY,
				password CHAR(255) NOT NULL,
				email VARCHAR(255) NOT NULL
			) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
			""";

	private static final String authTableStatement =
			"""
			CREATE TABLE IF NOT EXISTS auth (
				username VARCHAR(32) NOT NULL,
				authtoken VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY
			) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
			""";

	private static final String gamesTableStatement =
			"""
			CREATE TABLE IF NOT EXISTS games (
				gameid INT NOT NULL UNIQUE PRIMARY KEY,
				whiteusername VARCHAR(32),
				blackusername VARCHAR(32),
				gamename VARCHAR(100) NOT NULL,
				chessgame JSON NOT NULL
			) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
			""";
	private static final String[] createStatements = new String[]{ usersTableStatement, authTableStatement, gamesTableStatement };

	private static void configureDatabase() {
		try {
			DatabaseManager.createDatabase();
			try (var conn = DatabaseManager.getConnection()) {
				for (var statement : createStatements) {
					try (var ps = conn.prepareStatement(statement)) {
						ps.executeUpdate();
					}
				}
			} catch (SQLException e) {
				System.out.println(e);
				// TODO handle SQLException and notify user with 500 error code
				//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
			}
		} catch (DataAccessException e) {
			System.out.println(e);
		}
	}

	public MySQLDatabase() {
		configureDatabase();
	}

	public void clearUsers() {
		try (var conn = DatabaseManager.getConnection()) {
			String statement = "DROP TABLE IF EXISTS users";
			try (var ps = conn.prepareStatement(statement)) {
				ps.executeUpdate();
			}

			try (var ps = conn.prepareStatement(usersTableStatement)) {
				ps.executeUpdate();
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public void clearGames() {
		try (var conn = DatabaseManager.getConnection()) {
			String statement = "DROP TABLE IF EXISTS games";
			try (var ps = conn.prepareStatement(statement)) {
				ps.executeUpdate();
			}

			try (var ps = conn.prepareStatement(gamesTableStatement)) {
				ps.executeUpdate();
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public void clearAuth() {
		try (var conn = DatabaseManager.getConnection()) {
			String statement = "DROP TABLE IF EXISTS auth";
			try (var ps = conn.prepareStatement(statement)) {
				ps.executeUpdate();
			}

			try (var ps = conn.prepareStatement(authTableStatement)) {
				ps.executeUpdate();
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public void clear() {
		clearUsers();
		clearGames();
		clearAuth();
	}

	public void createUser(UserData user) {
		String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getEmail());

			ps.executeUpdate();
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public UserData getUser(String username) {
		String statement = "SELECT username, password, email FROM users WHERE username = ?";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, username);
			try (var rs = ps.executeQuery()) {
				if (rs.next()) return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}

		return null;
	}

	public void createGame(GameData game) {
		String chessGameJSON = new Gson().toJson(game.getGame());
		String statement = "INSERT INTO games (gameid, whiteusername, blackusername, gamename, chessgame) VALUES (?, ?, ?, ?, ?)";
		// TODO figure out what happens when usernames are null

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setInt(1, game.getGameID());
			ps.setString(2, game.getWhiteUsername());
			ps.setString(3, game.getBlackUsername());
			ps.setString(4, game.getGameName());
			ps.setString(5, chessGameJSON);

			ps.executeUpdate();

			//System.out.println(getGame(game.getGameID()));
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public GameData getGame(int gameId) {
		String statement = "SELECT gameid, whiteusername, blackusername, gamename, chessgame FROM games WHERE gameid = '" + gameId + "'";
		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement); var rs = ps.executeQuery()) {
			if (rs.next()) {
				ChessGame chessGame = new Gson().fromJson(rs.getString("chessgame"), ChessGame.class);
				return new GameData(rs.getInt("gameid"),
						rs.getString("whiteusername"),
						rs.getString("blackusername"),
						rs.getString("gamename"),
						chessGame);
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}

		return null;
	}

	public HashSet<GameData> listGames() {
		try (var conn = DatabaseManager.getConnection()) {
			HashSet<GameData> gameList = new HashSet<>();
			String statement = "SELECT gameid, whiteusername, blackusername, gamename, chessgame FROM games";

			try (var ps = conn.prepareStatement(statement)) {
				try (var rs = ps.executeQuery()) {
					while (rs.next()) {
						ChessGame chessGame = new Gson().fromJson(rs.getString("chessgame"), ChessGame.class);
						gameList.add(new GameData(rs.getInt("gameid"),
								rs.getString("whiteusername"),
								rs.getString("blackusername"),
								rs.getString("gamename"),
								chessGame));
					}
				}
			}

			return gameList;
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}

		return null;
	}

	public boolean userInGame(String username, int gameID, String clientColor) {
		GameData game = getGame(gameID);
		return game != null &&
			   (("white".equalsIgnoreCase(clientColor) && game.getWhiteUsername() != null) ||
				"black".equalsIgnoreCase(clientColor) && game.getBlackUsername() != null);
	}

	public void updateGame(String username, int gameID, String clientColor) {
		String columnToUpdate = ("white".equalsIgnoreCase(clientColor)) ? "whiteusername" : "blackusername";
		String statement = "UPDATE games SET " + columnToUpdate + " = ? WHERE gameid = ?";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, username); // Set the username to update
			ps.setInt(2, gameID); // Set the game ID for the WHERE clause

			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				// Handle case where the game ID does not exist or no rows were updated
				System.out.println("No rows updated, check if the game ID exists.");
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public void createAuth(AuthData authData) {
		String statement = "INSERT INTO auth (username, authtoken) VALUES (?, ?)";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, authData.getUsername());
			ps.setString(2, authData.getAuthToken());

			ps.executeUpdate();
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}

	public AuthData getAuth(String authToken) {
		String statement = "SELECT username, authtoken FROM auth WHERE authtoken = ?";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, authToken);

			try (var rs = ps.executeQuery()) {
				if (rs.next()) return new AuthData(rs.getString("username"), rs.getString("authtoken"));
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}

		return null;
	}

	public void deleteAuth(String authToken) {
		String statement = "DELETE FROM auth WHERE authtoken = ?";

		try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
			ps.setString(1, authToken);

			ps.executeUpdate();
		} catch (SQLException | DataAccessException e) {
			System.out.println(e);
			// TODO handle SQLException and notify user with 500 error code
			//throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
		}
	}
}
