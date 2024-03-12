package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
	public static HttpURLConnection createConnection(String endpoint, String method) throws Exception {
		URL url = new URL(Application.BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-Type", "application/json");

		if (method.equals("POST") || method.equals("PUT")) {
			connection.setDoOutput(true);
		}

		return connection;
	}

	public static HttpURLConnection createPostConnection(String endpoint) throws Exception {
		return createConnection(endpoint, "POST");
	}

	public static HttpURLConnection createGetConnection(String endpoint) throws Exception {
		return createConnection(endpoint, "GET");
	}

	public static HttpURLConnection createPutConnection(String endpoint) throws Exception {
		return createConnection(endpoint, "PUT");
	}

	public static HttpURLConnection createDeleteConnection(String endpoint) throws Exception {
		return createConnection(endpoint, "DELETE");
	}

	public static void sendRequest(HttpURLConnection connection, String requestBody) throws Exception {
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(requestBody.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	public static String readResponse(HttpURLConnection connection) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();
		return response.toString();
	}
}
