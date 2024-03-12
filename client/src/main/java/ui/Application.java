package ui;

public class Application {
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
	static final String BASE_URL = "http://localhost:4000";

	public Application() {
		currentState = State.PRE_LOGIN;
		preLoginUI = new PreLoginUI(this);
		/*postLoginUI = new PostLoginUI(this);
		gamePlayUI = new GamePlayUI(this);*/
	}

	public void run() {
		while (currentState != State.EXIT) {
			switch (currentState) {
				case PRE_LOGIN:
					preLoginUI.run();
					break;
				case POST_LOGIN:
					System.out.println(getAuthToken());
					exitApplication();
					break;
					/*postLoginUI.run();
					break;
				case GAME_PLAY:
					gamePlayUI.run();
					break;*/
			}
		}
		System.out.println("Exiting the application. Goodbye!");
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
		Application app = new Application();
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
