package ui;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
	private static final Gson gson = new Gson();

	public static void sendPostRequest(String endpoint, String requestBody,
									   ResponseCallback successCallback,
									   Runnable failureCallback) {
		try {
			HttpURLConnection connection = createPostConnection(endpoint);
			sendRequest(connection, requestBody);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String response = readResponse(connection);
				successCallback.onResponse(response);
			} else {
				failureCallback.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpURLConnection createPostConnection(String endpoint) throws Exception {
		URL url = new URL(Application.BASE_URL + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		return connection;
	}

	private static void sendRequest(HttpURLConnection connection, String requestBody) throws Exception {
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(requestBody.getBytes());
		outputStream.flush();
		outputStream.close();
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