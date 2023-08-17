package dev.pyojan.util;

import dev.pyojan.Main;
import dev.pyojan.config.Constant;
import dev.pyojan.gui.MessageGuiDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PropertyUtility {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtility.class);
    private final Properties properties;
    private final String filePath;

    public PropertyUtility(String filePath) {
        this.filePath = filePath;
        properties = new Properties();
        loadProperties();
    }
    public PropertyUtility() {
        this.filePath = Constant.SERVER_CONFIG_PATH;
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            properties.load(inputStream);
        } catch (IOException e) {
            // File does not exist, creating it
            createPropertyFileWithParentDirectories();
        }
    }

    private void createPropertyFileWithParentDirectories() {
        File propertyFile = new File(filePath);
        File parentDir = propertyFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean dirsCreated = parentDir.mkdirs();
            if (!dirsCreated) {
                MessageGuiDialog.showErrorDialog("Error while configuration", "Failed to create configuration.\nPlease restart application and try again.");
                logger.error("Failed to create configuration.");
            }
        }

        try {
            boolean fileCreated = propertyFile.createNewFile();
            if (!fileCreated) {
                MessageGuiDialog.showErrorDialog("Error while configuration", "Failed to create configuration.\nPlease restart application and try again.");
                logger.error("Failed to create configuration.");
            }
        } catch (IOException e) {
            MessageGuiDialog.showErrorDialog("Error while configuration", "Failed to create configuration.\nPlease check log file for more info");
            logger.error("Failed to create configuration." + e.getMessage());
        }
    }

    public String getPropertyWithKey(String key) {
        return properties.getProperty(key);
    }

    public void savePropertyWithKey(String key, String value) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            properties.setProperty(key, value);
            properties.store(outputStream, null);
        } catch (IOException e) {
            MessageGuiDialog.showErrorDialog("Error while configuration", "Failed to save configuration.\nPlease check log file for more info");
            logger.error("Failed to create configuration." + e.getMessage());
        }
    }

    public List<String> getAllProperties() {
        List<String> allProperties = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            allProperties.add(key + "=" + value);
        }
        return allProperties;
    }

    public void removePropertyByKey(String key) {
        properties.remove(key);
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            MessageGuiDialog.showErrorDialog("Error while configuration", "Failed to remove configuration.\nPlease check log file for more info");
            logger.error("Failed to create configuration." + e.getMessage());
        }
    }
}
