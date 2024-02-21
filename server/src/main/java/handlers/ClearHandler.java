package handlers;

import spark.Spark;

import service.ClearService;

public class ClearHandler {
	public static void clear(ClearService clearService) {
		Spark.delete("/db", (request, response) -> {
			boolean status = clearService.clear();

			if (!status) CreateResponse.halt500("Error: failed to clear database");
			else CreateResponse.response200(response, "{}");

			return response.body();
		});
	}
}
