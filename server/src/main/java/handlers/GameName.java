package handlers;

public record GameName(String gameName) {
	@Override
	public String toString() {
		return "GameName{" +
				"gameName='" + gameName + '\'' +
				'}';
	}
}
