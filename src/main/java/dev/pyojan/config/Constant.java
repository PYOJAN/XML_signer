package dev.pyojan.config;

import java.io.File;

public class Constant {
    private final static String SEPARATOR = File.separator;
    public final static String ROOT_PATH = System.getProperty("user.home") + SEPARATOR + ".cashLogin";
    public final static String SERVER_CONFIG_PATH = ROOT_PATH + SEPARATOR + "application.properties";
    public final static int DEFAULT_PORT = 1620;

    public static final String PORT_PROPERTY_KEY = "port";
    public static final String PKCS11_LIB_KEY = "library";
    public static final String COMMAND = "Pkinetworksign";

    public static final String IS_REMEMBER_PIN = "isRememberPin";

}
