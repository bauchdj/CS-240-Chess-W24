package dataAccessTests;

import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;

import chess.ChessGame;
import dataAccess.*;
import model.*;

public class DataAccessTests {
	private static final DataAccess db = new MySQLDatabase();
	private static final AuthDAO authDAO = new AuthDAO(db);
	private static final UserDAO userDAO = new UserDAO(db);
	private static final GameDAO gameDAO = new GameDAO(db);

	@BeforeEach
	public void setup() throws TestException {
		authDAO.clearAuth();
		userDAO.clearUsers();
		gameDAO.clearGames();
	}

	@Test
	@Order(1)
	@DisplayName("Create User")
	public void successCreateUser() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userDAO.createUser(userData);
		Assertions.assertEquals(userData.getUsername(), userDAO.getUser("user").getUsername());
	}

	@Test
	@Order(2)
	@DisplayName("Fail to Create Duplicate User")
	public void failCreateUser() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userDAO.createUser(userData);
		UserData duplicateUsername = new UserData("user", "pwd", "dup@chess.com");
		userDAO.createUser(duplicateUsername);
		Assertions.assertEquals(userData.getEmail(), userDAO.getUser("user").getEmail());
	}

	@Test
	@Order(3)
	@DisplayName("Get User")
	public void successGetUser() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userDAO.createUser(userData);
		Assertions.assertNotNull(userDAO.getUser("user"));
	}

	@Test
	@Order(4)
	@DisplayName("Non-existent User Get")
	public void failGetUser() throws TestException {
		Assertions.assertNull(userDAO.getUser("user"));
	}

	@Test
	@Order(5)
	@DisplayName("Success user should exist")
	public void successUserExists() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		userDAO.createUser(userData);
		Assertions.assertTrue(userDAO.userExists("user"));
	}

	@Test
	@Order(6)
	@DisplayName("Failure user should not exist")
	public void failUserExists() throws TestException {
		Assertions.assertFalse(userDAO.userExists("user"));
	}

	@Test
	@Order(7)
	@DisplayName("Create AuthData")
	public void successCreateAuthData() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		authDAO.createAuth(authData);
		Assertions.assertEquals(authData.getAuthToken(), authDAO.getAuth(authData).getAuthToken());
	}

	@Test
	@Order(8)
	@DisplayName("Fail to Create AuthData with duplicate authToken")
	public void failCreateAuthData() throws TestException {
		AuthData authData = new AuthData("one", "token-uuid");
		AuthData dupToken = new AuthData("two", "token-uuid");
		authDAO.createAuth(authData);
		authDAO.createAuth(dupToken);
		Assertions.assertEquals(authData.getUsername(), authDAO.getAuth(dupToken).getUsername());
	}

	@Test
	@Order(9)
	@DisplayName("Success Get AuthData")
	public void successGetAuthData() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		authDAO.createAuth(authData);
		Assertions.assertNotNull(authDAO.getAuth(authData));
	}

	@Test
	@Order(10)
	@DisplayName("Non-existent authToken")
	public void failGetAuthData() throws TestException {
		Assertions.assertNull(authDAO.getAuth(new AuthData("token")));
	}

	@Test
	@Order(11)
	@DisplayName("Success AuthData exists")
	public void successAuthExists() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		authDAO.createAuth(authData);
		Assertions.assertTrue(authDAO.authExists(authData));
	}

	@Test
	@Order(12)
	@DisplayName("Non-existent AuthData")
	public void failAuthExists() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		Assertions.assertFalse(authDAO.authExists(authData));
	}

	@Test
	@Order(13)
	@DisplayName("Success Delete AuthData")
	public void successDeleteAuth() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		authDAO.createAuth(authData);
		authDAO.deleteAuth(authData);
		Assertions.assertFalse(authDAO.authExists(authData));
	}

	@Test
	@Order(14)
	@DisplayName("Non-existent AuthData")
	public void failDeleteAuthData() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");
		AuthData secondAuth = new AuthData("user", "2nd");
		authDAO.createAuth(authData);
		authDAO.createAuth(secondAuth);
		authDAO.deleteAuth(authData);
		Assertions.assertTrue(authDAO.authExists(secondAuth));
	}

	@Test
	@Order(15)
	@DisplayName("Success Create Game")
	public void successCreateGame() throws TestException {

	}

	@Test
	@Order(16)
	@DisplayName("Invalid GameData cannot Create Game")
	public void failCreateGame() throws TestException {

	}

	@Test
	@Order(17)
	@DisplayName("Success Get Game")
	public void successGetGame() throws TestException {

	}

	@Test
	@Order(18)
	@DisplayName("Fail Get Game")
	public void failGetGame() throws TestException {

	}

	@Test
	@Order(19)
	@DisplayName("Success Game Exists")
	public void successGameExists() throws TestException {

	}

	@Test
	@Order(20)
	@DisplayName("Fail Game Exists")
	public void failGameExists() throws TestException {

	}

	@Test
	@Order(21)
	@DisplayName("Success List Game")
	public void successListGames() throws TestException {

	}

	@Test
	@Order(22)
	@DisplayName("Fail List Games")
	public void failListGames() throws TestException {

	}

	@Test
	@Order(21)
	@DisplayName("Success User Exists in Games")
	public void successGameDataUserExists() throws TestException {

	}

	@Test
	@Order(22)
	@DisplayName("Fail User Exists in Games")
	public void failGameDataUserExists() throws TestException {

	}

	@Test
	@Order(21)
	@DisplayName("Success Update Game")
	public void successUpdateGame() throws TestException {

	}

	@Test
	@Order(22)
	@DisplayName("Fail Update Game")
	public void failUpdateGame() throws TestException {

	}

	@Test
	@Order(23)
	@DisplayName("Clear Success Users")
	public void successClearUsers() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");

		userDAO.createUser(userData);

		userDAO.clearUsers();

		Assertions.assertNull(userDAO.getUser("user"));
	}
	@Test
	@Order(24)
	@DisplayName("Clear Success Auth")
	public void successClearAuth() throws TestException {
		AuthData authData = new AuthData("user", "token-uuid");

		authDAO.createAuth(authData);

		authDAO.clearAuth();

		Assertions.assertNull(authDAO.getAuth(authData));
	}
	@Test
	@Order(25)
	@DisplayName("Clear Success Games")
	public void successClearGames() throws TestException {
		GameData gameData = new GameData(1, "test", new ChessGame());

		gameDAO.createGame(gameData);

		gameDAO.clearGames();

		Assertions.assertTrue(gameDAO.listGames().isEmpty());
	}
}
