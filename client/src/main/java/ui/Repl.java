package ui;

import com.google.gson.Gson;
import java.util.Scanner;

public abstract class Repl {
	protected static final Scanner scanner = new Scanner(System.in);

	public void run() {
		while (true) {
			displayPrompt();
			String input = scanner.nextLine();

			if (shouldExit(input)) {
				break;
			}

			processInput(input);
		}
	}

	protected abstract void displayPrompt();

	protected abstract boolean shouldExit(String input);

	protected abstract void processInput(String input);
}
