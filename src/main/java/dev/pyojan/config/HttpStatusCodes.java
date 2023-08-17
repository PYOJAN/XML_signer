package dev.pyojan.config;

public class HttpStatusCodes {
    // Successful responses
    public static final int OK = 200;                   // Standard response for successful HTTP requests
    public static final int CREATED = 201;              // Request has been fulfilled, resulting in the creation of a new resource
    public static final int ACCEPTED = 202;             // Request has been accepted for processing, but the processing has not been completed
    public static final int NO_CONTENT = 204;           // Request succeeded, but there is no new information to send back

    // Redirection responses
    public static final int MOVED_PERMANENTLY = 301;     // Requested resource has been permanently moved to a different location
    public static final int FOUND = 302;                 // Requested resource has been temporarily moved to a different location
    public static final int SEE_OTHER = 303;             // Requested resource can be found under a different URI
    public static final int NOT_MODIFIED = 304;          // Requested resource has not been modified since the last request

    // Client error responses
    public static final int BAD_REQUEST = 400;           // Server could not understand the request due to invalid syntax or missing parameters
    public static final int UNAUTHORIZED = 401;          // Request requires user authentication
    public static final int FORBIDDEN = 403;             // Server understood the request but refuses to authorize it
    public static final int NOT_FOUND = 404;             // Requested resource could not be found
    public static final int METHOD_NOT_ALLOWED = 405;    // Request method is not supported for the requested resource
    public static final int CONFLICT = 409;              // Request conflicts with the current state of the server

    // Server error responses
    public static final int INTERNAL_SERVER_ERROR = 500; // Server encountered an unexpected condition that prevented it from fulfilling the request
    public static final int NOT_IMPLEMENTED = 501;       // Server does not support the functionality required to fulfill the request
    public static final int BAD_GATEWAY = 502;           // Server acting as a gateway received an invalid response from an upstream server
    public static final int SERVICE_UNAVAILABLE = 503;   // Server is currently unable to handle the request due to a temporary overload or maintenance
    public static final int GATEWAY_TIMEOUT = 504;       // Server acting as a gateway did not receive a timely response from an upstream server
}