package ui;

import java.util.Scanner;

public abstract class Repl {
	protected static final Scanner scanner = new Scanner(System.in);
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
				app.exitApplication();
				break;
			}

			processInput(input);

			if (shouldNavigate) break;
		}
	}

	protected abstract void displayPrompt();

	protected abstract void processInput(String input);

	protected void navigate() {
		shouldNavigate = true;
	}
}
