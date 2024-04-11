package connection;

import ui.GamePlayUI;
import webSocketMessages.serverMessages.*;

import com.google.gson.Gson;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketConnection {
	private static final Gson gson = new Gson();
	private static URI baseUri;
	private String authToken = null;
	private GamePlayUI gamePlayUI = null;
	private Session session = null;

	public WebSocketConnection(GamePlayUI gamePlayUI, String authToken) {
		this.authToken = authToken;
		this.gamePlayUI = gamePlayUI;
	}

	public static void setBaseUri(String host, int port, String path) {
		try {
			baseUri = new URI("ws://" + host + ":" + port + path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void connect() {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, baseUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to WebSocket server");
		this.session = session;
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		handleMessage(message);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Disconnected from WebSocket server");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.out.println("WebSocket error occurred");
		throwable.printStackTrace();
	}



	public void handleMessage(String message) {
		System.out.println("Message received: " + message);

		if (message == "chicken lips") {
			onOpen(session);
			onMessage(session, message);
			//onClose(session, new CloseReason(new CloseCode(), "nope"));
			//onError();
		}

		ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

		switch (serverMessage.getServerMessageType()) {
			case LOAD_GAME:
				gamePlayUI.handleLoadGame(gson.fromJson(message, LoadGame.class));
				break;
			case ERROR:
				gamePlayUI.handleError(gson.fromJson(message, ServerMessageError.class));
				break;
			case NOTIFICATION:
				gamePlayUI.handleNotification(gson.fromJson(message, Notification.class));
				break;
			default:
				// Handle unknown message type
				System.out.println("Unknown message type! Yikes!");
		}
	}
}
