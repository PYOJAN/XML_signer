package dev.pyojan.controller;

import dev.pyojan.config.HttpStatusCodes;
import dev.pyojan.entity.response.ApiResponse;
import dev.pyojan.util.Utils;
import spark.Request;
import spark.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorsController {
 private static final Logger logger = LoggerFactory.getLogger(ErrorsController.class);
    public static String notFound(Request req, Response res) {
        res.status(HttpStatusCodes.NOT_IMPLEMENTED);
        Utils.setContentTypeXml(req, res);
        return ApiResponse.error( "", "OT-04", String.format("METHOD: [%s] - PATH: [%s]", req.requestMethod(), req.pathInfo()));
    }

    public static String internalServerError(Request req, Response res) {
        res.status(HttpStatusCodes.INTERNAL_SERVER_ERROR);
        Utils.setContentTypeXml(req, res);
        logger.error("[OT-05] - Internal server error occurred");
        return ApiResponse.error( "", "OT-05", "Internal server error occurred");
    }

    public static void exception(RuntimeException exception, Request req, Response res) {
        res.status(HttpStatusCodes.OK);
        Utils.setContentTypeXml(req, res);
        res.body(exception.getMessage());

        String errorValue = Utils.extractErrorValue(exception.getMessage());
        if (errorValue != null) {
            exception.printStackTrace();
            logger.error(errorValue);
        } else {
            exception.printStackTrace();
            logger.error(exception.getMessage());
        }
    }

}
