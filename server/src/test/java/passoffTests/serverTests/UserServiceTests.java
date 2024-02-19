package passoffTests.serverTests;

import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;

import dataAccess.Database;
import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import service.UserService;
import model.UserData;
import model.AuthData;

public class UserServiceTests {
	private static final Database db = new Database();
	private static final UserDAO userDAO = new UserDAO(db);
	private static final AuthDAO authDAO = new AuthDAO(db);
	private static final UserService userService = new UserService(userDAO, authDAO);

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
		// Empty username
		Assertions.assertNull(userService.register(new UserData("", "pwd", "user@chess.com")));
		// Empty password
		Assertions.assertNull(userService.register(new UserData("user", "", "user@chess.com")));
		// Empty email
		Assertions.assertNull(userService.register(new UserData("user", "pwd", "")));
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
	@Order(3)
	@DisplayName("Non-existent User Login")
	public void loginInvalidUser() throws TestException {
		UserData userData = new UserData("user", "pwd", "user@chess.com");
		AuthData authData = userService.login(userData);
		Assertions.assertNull(authData);
	}

	
}
