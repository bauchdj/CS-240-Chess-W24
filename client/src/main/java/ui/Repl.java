package ui;

import com.google.gson.Gson;
import java.util.Scanner;

public abstract class Repl { // Read-Eval-Print Loop
	protected static final Scanner scanner = new Scanner(System.in);
	protected static final Gson gson = new Gson();
	protected App app;
	protected boolean shouldNavigate = false;

	public Repl(App app) {
		this.app = app;
	}

	public void run() {
		String quit = "quit";
		String help = "help";

		while (true) {
			displayPrompt();
			System.out.println("Type '" + quit + "' to exit");
			System.out.println("Type '" + help + "' for help");
			String input = UserInputHandler.getUserInput("Enter your choice: ");

			if (input.equalsIgnoreCase(quit)) {
				app.exitApplication(); // breaks the UI loop and App loop
				break;
			}

			if (input.equalsIgnoreCase(help)) {
				displayHelp();
				continue;
			}

			processInput(input);

			if (shouldNavigate) break; // breaks the loop of UI
		}
	}

	protected abstract void displayPrompt();

	protected abstract void displayHelp();

	protected abstract void processInput(String input);

	protected void navigate() {
		shouldNavigate = true;
	}
}
