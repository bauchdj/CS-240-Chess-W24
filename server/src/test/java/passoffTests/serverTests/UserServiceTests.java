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
	private final Database db = new Database();
	private final UserDAO userDAO = new UserDAO(db);
	private final AuthDAO authDAO = new AuthDAO(db);
	private final UserService userService = new UserService(userDAO, authDAO);

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
		Assertions.assertNull(userService.register(new UserData("", "pwd", "user@chess.com")));
		Assertions.assertNull(userService.register(new UserData("user", "", "user@chess.com")));
		Assertions.assertNull(userService.register(new UserData("user", "pwd", "")));
	}
}
