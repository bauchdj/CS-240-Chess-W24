package handlers;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.HashMap;
import java.util.HashSet;

@WebSocket
public class WebSocketHandler {
	public static HashMap<Integer, HashSet<Session>> mapGameIDToSessions = new HashMap<>();

	@OnWebSocketConnect
	public void onConnect(Session session) throws Exception {
		System.out.println("Client connected: " + session.getRemoteAddress());
		// Handle new WebSocket connection
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws Exception {
		WebSocketMessageHandler wsMsgHandler = new WebSocketMessageHandler(session);
		wsMsgHandler.handleMessage(message);
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