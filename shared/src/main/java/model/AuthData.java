package model;

import java.util.Objects;

public class AuthData {
	private String username;
	private String authToken;

	public AuthData(String username, String authToken) {
		setAuthToken(authToken);
		setUsername(username);
	}

	public AuthData(String authToken) {
		setAuthToken(authToken);
		setUsername(null);
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "AuthData{" +
				"authToken='" + authToken + '\'' +
				", username='" + username + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AuthData authData = (AuthData) o;
		return Objects.equals(authToken, authData.authToken) && Objects.equals(username, authData.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(authToken, username);
	}
}
