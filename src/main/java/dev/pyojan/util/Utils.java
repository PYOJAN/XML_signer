package dev.pyojan.util;

import lombok.Getter;
import lombok.Setter;
import spark.Request;
import spark.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Utils {

    private static char[] tokenPin = null;
    public static boolean isRememberPin = false;

    public static void setContentTypeXml(Request req, Response res) {
        boolean isOrigin = req.headers("Origin") != null && req.headers("Origin").contains("capricorn.cash");
        res.type(isOrigin ? "text/html" : "application/xml");
    }

    public static String extractErrorValue(String xmlResponse) {
        if(xmlResponse != null && xmlResponse.startsWith("<error>")) {

            Pattern pattern = Pattern.compile("<error[^>]*>(.*?)</error>");
            Matcher matcher = pattern.matcher(xmlResponse);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
