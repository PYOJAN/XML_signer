package dev.pyojan.gui;

import dev.pyojan.Main;

import javax.swing.*;
import java.util.Objects;

public class MessageGuiDialog {


    public static void showErrorDialog(String title, String message) {
        dialog(title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessDialog(String message) {
        dialog("Success", message, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showSuccessDialog(String title, String message) {
        dialog(title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void dialog(String title, String message, int type) {
        JFrame parentFrame = new JFrame();

        ClassLoader classLoader = Main.class.getClassLoader();
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(classLoader.getResource("logo.png")));
        parentFrame.setIconImage(imageIcon.getImage());

        parentFrame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(parentFrame, message, title, type);
        parentFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                System.exit(1);
            }
        });
    }
}
