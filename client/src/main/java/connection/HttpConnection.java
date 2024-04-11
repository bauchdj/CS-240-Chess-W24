package connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
	private static String authToken = null;
	public static String baseUrl = null;

	public static void setBaseUrl(String host, int port) {
		baseUrl = "http://" + host + ":" + port;
	}

	public static void setAuthToken(String token) {
		authToken = token;
	}

	public static void sendGetRequest(String endpoint,
									  ResponseCallback successCallback,
									  Runnable failureCallback) {
		sendRequest(endpoint, "GET", null, successCallback, failureCallback);
	}

	public static void sendPostRequest(String endpoint, String requestBody,
									   ResponseCallback successCallback,
									   Runnable failureCallback) {
		sendRequest(endpoint, "POST", requestBody, successCallback, failureCallback);
	}

	public static void sendPutRequest(String endpoint, String requestBody,
									  ResponseCallback successCallback,
									  Runnable failureCallback) {
		sendRequest(endpoint, "PUT", requestBody, successCallback, failureCallback);
	}

	public static void sendDeleteRequest(String endpoint,
										 ResponseCallback successCallback,
										 Runnable failureCallback) {
		sendRequest(endpoint, "DELETE", null, successCallback, failureCallback);
	}

	private static void sendRequest(String endpoint, String method, String requestBody,
									ResponseCallback successCallback,
									Runnable failureCallback) {
		try {
			HttpURLConnection connection = createConnection(endpoint, method);
			if (requestBody != null) {
				sendRequestBody(connection, requestBody);
			}
			handleResponse(connection, successCallback, failureCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpURLConnection createConnection(String endpoint, String method) throws Exception {
		URL url = new URL(baseUrl + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		if (method.equals("POST") || method.equals("PUT")) {
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
		}
		if (authToken != null) {
			connection.setRequestProperty("Authorization", authToken);
		}
		return connection;
	}

	private static void sendRequestBody(HttpURLConnection connection, String requestBody) throws Exception {
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(requestBody.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	private static void handleResponse(HttpURLConnection connection,
									   ResponseCallback successCallback,
									   Runnable failureCallback) throws Exception {
		int responseCode = connection.getResponseCode();
		//System.out.println("\n>>> Status code: " + responseCode + '\n');
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String response = readResponse(connection);
			successCallback.onResponse(response);
		} else {
			// if status code is 403 that means it is already take. Pass the status code to the callback
			failureCallback.run();
		}
	}

	private static String readResponse(HttpURLConnection connection) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();
		return response.toString();
	}

	public interface ResponseCallback {
		void onResponse(String response);
	}
}