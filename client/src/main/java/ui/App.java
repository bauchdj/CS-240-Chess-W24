package ui;

public class App {
	private enum State {
		PRE_LOGIN,
		POST_LOGIN,
		GAME_PLAY,
		EXIT
	}

	private State currentState;
	private PreLoginUI preLoginUI;
	private PostLoginUI postLoginUI;
	private GamePlayUI gamePlayUI;
	private String authToken;
	public static String BASE_URL = "http://localhost:" + String.valueOf(4000);

	public App(int port) {
		currentState = State.PRE_LOGIN;
		preLoginUI = new PreLoginUI(this);
		postLoginUI = new PostLoginUI(this);
		gamePlayUI = new GamePlayUI(this);
		setBaseUrl("localhost", port);
	}

	public void run() {
		while (currentState != State.EXIT) {
			switch (currentState) {
				case PRE_LOGIN:
					preLoginUI.run();
					break;
				case POST_LOGIN:
					postLoginUI.run();
					break;
				case GAME_PLAY:
					gamePlayUI.run();
					break;
			}
		}
		System.out.println("Exiting the application. Goodbye!");
	}

	public static void setBaseUrl(String host, int port) {
		BASE_URL = "http://" + host + ":" + port;
	}

	public void navigateToPostLogin() {
		currentState = State.POST_LOGIN;
	}

	public void navigateToGamePlay() {
		currentState = State.GAME_PLAY;
	}

	public void navigateToPreLogin() {
		currentState = State.PRE_LOGIN;
	}
	public void exitApplication() {
		currentState = State.EXIT;
	}

	public static void main(String[] args) {
		App app = new App(4000);
		app.run();
	}

	public void storeAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void removeAuthToken() {
		storeAuthToken(null);
	}

	public String getAuthToken() {
		return authToken;
	}
}
