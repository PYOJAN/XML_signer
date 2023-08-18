package dev.pyojan.service;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import dev.pyojan.config.Config;
import dev.pyojan.config.Constant;
import dev.pyojan.gui.MessageGuiDialog;
import dev.pyojan.util.PropertyUtility;
import spark.Spark;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ShortcutRegister extends PropertyUtility {
    public ShortcutRegister() {
        super(Constant.SERVER_CONFIG_PATH);
    }

    public void register() {
        Provider provider = Provider.getCurrentProvider(false);

        // Register shortcut for PKCS11Chooser
        KeyStroke pkcs11KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK);
        provider.register(pkcs11KeyStroke, new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                final Config config = new Config();
                config.pkcs11Chooser();
            }
        });

        // Register shortcut for graceful shutdown and application exit
        KeyStroke shutdownKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK);
        provider.register(shutdownKeyStroke, new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                // Gracefully shutdown SparkJava server
                Spark.stop();
                MessageGuiDialog.showSuccessDialog("Server stop successfully.");
                // Exit the application
                System.exit(0);
            }
        });
    }

}
