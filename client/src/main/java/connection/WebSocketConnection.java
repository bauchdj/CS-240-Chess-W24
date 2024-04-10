package connection;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketConnection {
	private static URI baseUri;
	private Session session;

	public static void setBaseUri(String host, int port, String path) {
		try {
			baseUri = new URI("ws://" + host + ":" + port + path);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void onMessage(String message, Session session) {
		System.out.println("Received message: " + message);
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
}
