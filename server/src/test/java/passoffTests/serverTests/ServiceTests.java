package passoffTests.serverTests;

import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;

import chess.ChessGame;
import dataAccess.*;
import service.*;
import model.*;

public class ServiceTests {
	private static final Database db = new Database();
	private static final AuthDAO authDAO = new AuthDAO(db);
	private static final UserDAO userDAO = new UserDAO(db);
	private static final GameDAO gameDAO = new GameDAO(db);
	private static final UserService userService = new UserService(userDAO, authDAO);
	private static final GameService gameService = new GameService(gameDAO, authDAO);
	private static final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

	@BeforeEach
	public void setup() throws TestException {
		authDAO.clearAuth();
		userDAO.clearUsers();
		gameDAO.clearGames();
	}

	@Test
	@Order(1)
	@DisplayName("Register Success")
	public void successRegister() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		Assertions.assertEquals(userData, userDAO.getUser("user"));
	}

	@Test
	@Order(2)
	@DisplayName("Empty Password")
	public void failRegister() throws TestException {
		UserData emptyUsername = new UserData("", "pwd", "user@chess.com");
		Assertions.assertNull(userService.register(emptyUsername));

		UserData emptyPassword = new UserData("user", "", "user@chess.com");
		Assertions.assertNull(userService.register(emptyPassword));

		UserData emptyEmail = new UserData("user", "pwd", "");
		Assertions.assertNull(userService.register(emptyEmail));

		UserData user = new UserData("user", "pwd", "user@chess.com");
		userService.register(user);
		Assertions.assertNull(userService.register(user));
	}

	@Test
	@Order(3)
	@DisplayName("Login Success")
	public void successLogin() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		Assertions.assertNotNull(authData);
	}

	@Test
	@Order(4)
	@DisplayName("Non-existent User Login")
	public void loginInvalidUser() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		AuthData authData = userService.login(userData);
		Assertions.assertNull(authData);
	}

	@Test
	@Order(5)
	@DisplayName("Logout Success")
	public void successLogout() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		boolean isLogout = userService.logout(authData);
		Assertions.assertTrue(isLogout);
	}

	@Test
	@Order(6)
	@DisplayName("Logout Invalid Auth")
	public void logoutInvalidLogout() throws TestException {
		// second logout should fail
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		userService.logout(authData);
		boolean isLogout = userService.logout(authData);
		Assertions.assertFalse(isLogout);
	}

	@Test
	@Order(7)
	@DisplayName("Create Game Success")
	public void successCreateGame() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);

		GameID gameID = gameService.createGame(authData, "good game");
		Assertions.assertNotNull(gameID);
	}

	@Test
	@Order(8)
	@DisplayName("Create Game Failure")
	public void invalidCreateGame() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);

		// Create Game with Empty Name
		GameID emptyNameGameID = gameService.createGame(authData, "");
		Assertions.assertNull(emptyNameGameID);

		// Invalid auth after logout
		userService.logout(authData);
		GameID invalidAuthGameID = gameService.createGame(authData, "good game");
		Assertions.assertNull(invalidAuthGameID);
	}

	@Test
	@Order(9)
	@DisplayName("Join Game Success")
	public void successJoinGame() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		GameID gameID = gameService.createGame(authData, "good game");

		Assertions.assertNotNull(
				gameService.joinGame(authData, ChessGame.TeamColor.WHITE.toString(), gameID.getGameID()));
		Assertions.assertNotNull(
				gameService.joinGame(authData, ChessGame.TeamColor.BLACK.toString(), gameID.getGameID()));
		Assertions.assertNotNull(
				gameService.joinGame(authData, null, gameID.getGameID()));
	}

	@Test
	@Order(10)
	@DisplayName("Join Game Invalid")
	public void invalidJoinGame() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		GameID gameID = gameService.createGame(authData, "good game");

		Assertions.assertEquals("game does not exist",
				gameService.joinGame(authData, ChessGame.TeamColor.WHITE.toString(), 0));
		Assertions.assertEquals("game does not exist",
				gameService.joinGame(authData, ChessGame.TeamColor.BLACK.toString(), 0));

		// Same user cannot join twice under same team
		gameService.joinGame(authData, ChessGame.TeamColor.WHITE.toString(), gameID.getGameID());
		Assertions.assertEquals("already taken",
				gameService.joinGame(authData, ChessGame.TeamColor.WHITE.toString(), gameID.getGameID()));

		// Invalid auth after logout
		userService.logout(authData);
		Assertions.assertEquals("unauthorized",
				gameService.joinGame(authData, ChessGame.TeamColor.WHITE.toString(), gameID.getGameID()));
		Assertions.assertEquals("unauthorized",
				gameService.joinGame(authData, ChessGame.TeamColor.BLACK.toString(), gameID.getGameID()));
	}

	@Test
	@Order(11)
	@DisplayName("List Games")
	public void successListGames() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		gameService.createGame(authData, "gamez");

		Assertions.assertNotNull(gameService.listGames(authData));
		Assertions.assertFalse(gameService.listGames(authData).isEmpty());
	}

	@Test
	@Order(12)
	@DisplayName("List Games Invalid")
	public void invalidListGames() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		gameService.createGame(authData, "gamez");

		// Invalid auth after logout
		userService.logout(authData);
		Assertions.assertNull(gameService.listGames(authData));
	}

	@Test
	@Order(13)
	@DisplayName("Clear Success")
	public void successClear() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userService.register(userData);
		AuthData authData = userService.login(userData);
		gameService.createGame(authData, "g");

		Assertions.assertTrue(clearService.clear());

		Assertions.assertNull(gameService.listGames(authData));
	}

	// Don't know how to test clear failing.
}
