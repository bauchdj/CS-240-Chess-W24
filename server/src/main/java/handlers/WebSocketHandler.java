package handlers;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebSocketHandler {

	@OnWebSocketConnect
	public void onConnect(Session session) throws Exception {
		System.out.println("Client connected: " + session.getRemoteAddress());
		// Handle new WebSocket connection
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws Exception {
		System.out.println("Message received: " + message);
		// Handle incoming WebSocket message
		// You can send a response back to the client using session.getRemote().sendString(response);
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) throws Exception {
		System.out.println("Client disconnected: " + session.getRemoteAddress());
		// Handle WebSocket connection close
	}

	@OnWebSocketError
	public void onError(Session session, Throwable error) {
		System.err.println("WebSocket error occurred: " + error.getMessage());
		// Handle WebSocket error
	}
}