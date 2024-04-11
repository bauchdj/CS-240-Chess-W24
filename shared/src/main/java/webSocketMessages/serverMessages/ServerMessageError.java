package webSocketMessages.serverMessages;

import webSocketMessages.serverMessages.ServerMessage;

public class Error extends ServerMessage {
	private String errorMessage;

	public Error(String errorMessage) {
		super(ServerMessage.ServerMessageType.ERROR);
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
