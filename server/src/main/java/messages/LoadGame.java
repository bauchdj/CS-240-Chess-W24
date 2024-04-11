package messages;

import chess.ChessGame;
import webSocketMessages.serverMessages.ServerMessage;

public class LoadGame extends ServerMessage {
	private ChessGame game;

	public LoadGame(ChessGame game) {
		super(ServerMessageType.LOAD_GAME);
		this.game = game;
	}

	public ChessGame getGame() {
		return game;
	}
}
