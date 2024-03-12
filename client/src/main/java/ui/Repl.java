package ui;

import com.google.gson.Gson;
import java.util.Scanner;

public abstract class Repl { // Read-Eval-Print Loop
	protected static final Scanner scanner = new Scanner(System.in);
	protected static final Gson gson = new Gson();
	protected Application app;
	protected boolean shouldNavigate = false;

	public Repl(Application app) {
		this.app = app;
	}

	public void run() {
		while (true) {
			displayPrompt();
			String input = scanner.nextLine();

			if (input.equalsIgnoreCase("quit")) {
				app.exitApplication(); // breaks the UI loop and App loop
				break;
			}

			processInput(input);

			if (shouldNavigate) break; // breaks the loop of UI
		}
	}

	protected abstract void displayPrompt();

	protected abstract void processInput(String input);

	protected void navigate() {
		shouldNavigate = true;
	}
}
