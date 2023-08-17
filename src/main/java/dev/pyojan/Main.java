package dev.pyojan;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dev.pyojan.config.Config;
import dev.pyojan.config.Constant;
import dev.pyojan.controller.ApiController;
import dev.pyojan.controller.ErrorsController;
import dev.pyojan.gui.MessageGuiDialog;
import dev.pyojan.service.ShortcutRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import java.util.function.Consumer;

import static spark.Spark.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static int PORT = 1620;

    public static void main(String[] args) {
        // GUI theme
        FlatMacDarkLaf.setup();

        String javaVersion = System.getProperty("java.version");
        if (javaVersion.startsWith("1.8")) {
            Config config = new Config();
            new ShortcutRegister().register();
            PORT = Integer.parseInt(config.getPropertyWithKey(Constant.PORT_PROPERTY_KEY));
            port(PORT);
            initExceptionHandler(initExceptionHandler);
            // ==== API CONTROLLER ====
            ApiController.registerV1ApiRoutes();

            // ==== ERROR HANDLER =====
            notFound(ErrorsController::notFound);
            internalServerError(ErrorsController::internalServerError);
            exception(RuntimeException.class, ErrorsController::exception);

            // Run Spark application
            awaitInitialization();
            String message = String.format(
                    "Server start successfully on port: %s" +
                            "<br/><br/>Use combination of CTRL + ATL + Q to stop the Application." +
                            "<br/><br/>Use combination of CTRL + ATL + I to update PKCS11 path.", PORT);
            MessageGuiDialog.showSuccessDialog("XML Signer and login","<html>"+message+"</html>");
            logger.info("Server Start on the port: " + PORT);
        } else {
            MessageGuiDialog.showErrorDialog("Application Startup Error", "This application requires Java version 8, but the current version is " + javaVersion + ".");
            logger.error("This application requires Java version 8, but the current version is " + javaVersion + ".");
            System.exit(1);
        }
    }
    private static final Consumer<Exception> initExceptionHandler = (e) -> {
        String message = "Failed to start application. Port " + PORT + " is already in use.";
        MessageGuiDialog.showErrorDialog("Application Startup Error", message);
        logger.error(message);
        System.exit(100);
    };
}