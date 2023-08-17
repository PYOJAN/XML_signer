package dev.pyojan.config;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import dev.pyojan.gui.Pkcss11Picker;
import dev.pyojan.service.SigningService;
import dev.pyojan.util.PropertyUtility;
import spark.Spark;

public class Config extends PropertyUtility {
    public Config() {
        super(Constant.SERVER_CONFIG_PATH);
        defaultConfigurationProperties();
    }

    // Create default configuration
    private void defaultConfigurationProperties() {
        if (getPropertyWithKey(Constant.PORT_PROPERTY_KEY) == null) {
            savePropertyWithKey(Constant.PORT_PROPERTY_KEY, Integer.toString(Constant.DEFAULT_PORT));
        }
        if (getPropertyWithKey(Constant.PKCS11_LIB_KEY) == null || getPropertyWithKey(Constant.PKCS11_LIB_KEY).isEmpty()) {
            pkcs11Chooser();
        }
    }

    public String pkcs11Chooser() {
        String filePath = "";
        Pkcss11Picker pkcss11Picker = new Pkcss11Picker("PKCSS#11 Chooser", getPropertyWithKey(Constant.PKCS11_LIB_KEY));

        filePath = pkcss11Picker.getFilePath();
        if (!filePath.isEmpty()) {
            savePropertyWithKey(Constant.PKCS11_LIB_KEY, filePath);
            SigningService.performLogout();
        }
        return filePath;
    }


}
