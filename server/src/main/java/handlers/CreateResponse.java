package handlers;

import spark.Response;
import static spark.Spark.halt;
import com.google.gson.Gson;

public class CreateResponse {
	static class ErrorResponse {
		private String message;
		public ErrorResponse(String message) {
			this.message = message;
		}
	}

	private static void setStatus(Response res, int statusCode) {
		res.status(statusCode);
	}

	private static void setType(Response res, String type) {
		res.type(type);
	}

	private static String gson(Object obj) {
		return new Gson().toJson(obj);
	}

	private static ErrorResponse errorResponse(String err) {
		return new ErrorResponse(err);
	}

	public static void createResponse(Response res, int statusCode, String type, Object obj) {
		setStatus(res, statusCode);
		setType(res, type);
		if (obj instanceof String) res.body(obj.toString());
		else res.body(gson(obj));
	}

	public static void response200(Response res, Object obj) {
		createResponse(res, 200, "application/json", obj);
	}

	public static void createHalt(int statusCode, Object obj) {
		String body;
		if (obj instanceof String) body = obj.toString();
		else body = gson(obj);

		halt(statusCode, body);
	}

	public static void halt400() {
		createHalt(400,  errorResponse("Error: bad request"));
	}

	public static void halt400Msg(String err) {
		createHalt(400, errorResponse(err));
	}

	public static void halt401() {
		createHalt(401, errorResponse("Error: unauthorized"));
	}

	public static void haltUnauthorized(String authToken) {
		if (authToken == null || authToken.isEmpty()) halt401();
	}

	public static void halt403() {
		createHalt(403, errorResponse("Error: already taken"));
	}

	public static void halt500(String err) {
		createHalt(500,  errorResponse(err));
	}
}
