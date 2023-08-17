package dev.pyojan.entity.response;

import dev.pyojan.config.Constant;

import java.util.Date;

public class ApiResponse {

    public static String error(String txn, String errorCode, String message) {

        return String.format("<response><command>pkiNetworkSign</command><ts>%s</ts><txn>%s</txn><status>failed</status><error code='%s'>%s</error></response>", new Date(), txn, errorCode, message);

    }

    public static String success(String txn, String timestamp, String signedData) {
        return String.format("<response><command>%s</command><ts>%s</ts><txn>%s</txn><status>ok</status><file><attribute name=\"type\">xml</attribute></file><data>%s</data></response>", Constant.COMMAND, timestamp, txn, signedData);
    }
}
