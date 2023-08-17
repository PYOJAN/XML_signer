package dev.pyojan.gui;

import lombok.Setter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Pkcss11Picker extends JDialog {
    private JPanel main;
    private JTextField filePathHolder;
    private JButton filePickerButton;
    private JButton cancelButton;
    private JButton saveButton;
    private JLabel errorMessage;

    public Pkcss11Picker(String title, String existingPKCS11) {
        filePathHolder.setText(existingPKCS11);
        init(title);
        pack();
        setVisible(true);
    }

    private static boolean isValidFileExtension(String fileName) {
        return fileName.endsWith(".dll") || fileName.endsWith(".so") || fileName.endsWith(".dylib");
    }

    private void init(String title) {
        setContentPane(main);
        setTitle(title);
        setAlwaysOnTop(true);
        setModal(true);

        // Window side [Input box size is 300]
        int width = 300;
        int height = 180;
        setPreferredSize(new Dimension(width, height));
        setResizable(false);

        // Calculate screen center
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        int x = (int) ((bounds.getWidth() - width) / 2);
        int y = (int) ((bounds.getHeight() - height) / 2);
        setLocation(x, y);

        getRootPane().setDefaultButton(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        main.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        filePickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooser();
            }
        });
    }

    public void onSave() {
        filePathHolder.getText();
        dispose();
    }

    public void onCancel() {
        dispose();
    }

    public String getFilePath() {
        return filePathHolder.getText();
    }

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PKCS#11 Files", "dll", "so", "dylib"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                if (!isValidFileExtension(selectedFile.getAbsolutePath())) {
                    errorMessage.setText("Invalid file selected");
                } else {
                    filePathHolder.setText(selectedFile.getAbsolutePath());
                }
            }
        }
    }
}
