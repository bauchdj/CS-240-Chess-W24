package webSocketMessages.serverMessages;

public class ServerMessageError extends ServerMessage {
	private String errorMessage;

	public ServerMessageError(String errorMessage) {
		super(ServerMessage.ServerMessageType.ERROR);
		this.errorMessage = "Error: " + errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
