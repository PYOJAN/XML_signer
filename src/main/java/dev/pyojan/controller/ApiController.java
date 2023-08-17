package dev.pyojan.controller;

import dev.pyojan.service.SigningService;
import dev.pyojan.util.Utils;
import spark.Request;
import spark.Response;

import java.net.URLDecoder;

import static spark.Spark.*;

public class ApiController {
    public static void registerV1ApiRoutes() {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*"); // Change * to a specific origin if needed
            response.header("Access-Control-Request-Method", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
            // You can add more headers as needed
            response.type("application/json"); // Set the content type for your responses
        });


        post("/", SigningService::signXml);
        options("/", (Request req, Response res) -> req.body());
        after(Utils::setContentTypeXml);
    }
}
