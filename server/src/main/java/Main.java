import server.Server;

public class Main {
	private static Server server;

	public static void main(String[] args) {
		server = new Server();
		var port = server.run(4000);
		System.out.println("Started test HTTP server on " + port);
	}
}
