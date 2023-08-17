package dev.pyojan.config;

public class ErrorCodes {
    public static class Application {

        public static class Code {
            public static String A_1000 = "A-1000";
            public static String A_1001 = "A-1001";
        }

        public static class Msg {
            public static String A_1000 = "Internal server error";
            public static String A_1001 = "Unsupported request method";
        }
    }

    public static class Request {

        public static class Code {
            public static String R_1000 = "R-1000";
            public static String R_1001 = "R-1001";
        }

        public static class Msg {
            public static String R_1000 = "Invalid or unsupported request URL";
            public static String R_1001 = "Invalid request format/value";
        }
    }
}