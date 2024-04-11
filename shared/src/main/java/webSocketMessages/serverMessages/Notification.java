package webSocketMessages.serverMessages;

import webSocketMessages.serverMessages.ServerMessage;

public class Notification extends ServerMessage {
	private String message;

	public Notification(String message) {
		super(ServerMessage.ServerMessageType.NOTIFICATION);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
