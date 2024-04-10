package ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import connection.HttpConnection;
import model.*;

public class ServerFacade {
	private static final Gson gson = new Gson();

	public ServerFacade(int serverPort) {
		HttpConnection.setBaseUrl("localhost", serverPort);
	}

	public AuthData register(String username, String password, String email) throws Exception {
		UserData userData = new UserData(username, password, email);
		String requestBody = gson.toJson(userData);

		ResponseData responseData = new ResponseData();
		HttpConnection.sendPostRequest("/user", requestBody, responseData::setResponse, () -> {
			responseData.setError("Registration failed.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		String authToken = extractAuthToken(responseData.getResponse());
		storeAuthToken(authToken);
		return new AuthData(authToken);
	}

	public AuthData login(String username, String password) throws Exception {
		UserData userData = new UserData(username, password);
		String requestBody = gson.toJson(userData);

		ResponseData responseData = new ResponseData();
		HttpConnection.sendPostRequest("/session", requestBody, responseData::setResponse, () -> {
			responseData.setError("Login failed.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		String authToken = extractAuthToken(responseData.getResponse());
		storeAuthToken(authToken);
		return new AuthData(authToken);
	}

	public boolean createGame(String gameName) throws Exception {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameName", gameName);

		ResponseData responseData = new ResponseData();
		HttpConnection.sendPostRequest("/game", requestBody.toString(), responseData::setResponse, () -> {
			responseData.setError("Failed to create game.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		return true;
	}

	public boolean listGames() throws Exception {
		ResponseData responseData = new ResponseData();
		HttpConnection.sendGetRequest("/game", responseData::setResponse, () -> {
			responseData.setError("Failed to join game.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		JsonObject jsonResponse = gson.fromJson(responseData.getResponse(), JsonObject.class);
		JsonArray gamesArray = jsonResponse.getAsJsonArray("games");

		return true;
	}

	public boolean joinGame(int gameId, String playerColor) throws Exception {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameID", gameId);
		requestBody.addProperty("playerColor", playerColor);

		ResponseData responseData = new ResponseData();
		HttpConnection.sendPutRequest("/game", requestBody.toString(), responseData::setResponse, () -> {
			responseData.setError("Failed to join game.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		return true;
	}

	public boolean observeGame(int gameId) throws Exception {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("gameID", gameId);

		ResponseData responseData = new ResponseData();
		HttpConnection.sendPutRequest("/game", requestBody.toString(), responseData::setResponse, () -> {
			responseData.setError("Failed to observe game.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		return true;
	}

	public boolean logout() throws Exception {
		ResponseData responseData = new ResponseData();
		HttpConnection.sendDeleteRequest("/session", responseData::setResponse, () -> {
			responseData.setError("Logout failed.");
		});

		if (responseData.getError() != null) {
			throw new Exception(responseData.getError());
		}

		return true;
	}

	public void storeAuthToken(String token) {
		HttpConnection.setAuthToken(token);
	}

	private String extractAuthToken(String response) {
		return gson.fromJson(response, JsonObject.class)
				.get("authToken").getAsString();
	}

	private static class ResponseData {
		private String response;
		private String error;

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}
	}
}