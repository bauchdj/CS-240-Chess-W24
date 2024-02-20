package handlers;

import com.google.gson.Gson;
import spark.Spark;

import service.ClearService;

public class ClearHandler {
	public static void clear(ClearService clearService) {
		Spark.delete("/db", (request, response) -> {
			boolean status = clearService.clear();

			if (!status) {
				response.status(500);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: failed to clear database"));
			}

			response.status(200);
			response.type("application/json");
			return "{}";
		});
	}
}
