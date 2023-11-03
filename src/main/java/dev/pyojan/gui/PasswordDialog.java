package dev.pyojan.gui;

import dev.pyojan.Main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;


public class PasswordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField passwordField;
    //    private JLabel errorMessage;
    private boolean userCancelled = false;

    public PasswordDialog() {
        init(null);
        pack();
    }

    public PasswordDialog(String title) {

        init(title);
        pack();

    }

    public static void main(String[] args) {
        PasswordDialog dialog = new PasswordDialog("Token PIN");
//        dialog.setVisible(true);
        System.exit(0);
    }

    private void init(String title) {
        setContentPane(contentPane);
        setModal(true);
        setTitle(title);
        setAlwaysOnTop(true);

        ClassLoader classLoader = Main.class.getClassLoader();
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(classLoader.getResource("logo.png")));
        setIconImage(imageIcon.getImage());

        // Window side [Input box size is 300]
        int width = 280;
        int height = 190;
        setPreferredSize(new Dimension(width, height));

        // Calculate screen center
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        int x = (int) ((bounds.getWidth() - width) / 2);
        int y = (int) ((bounds.getHeight() - height) / 2);
        setLocation(x, y);


        getRootPane().setDefaultButton(buttonOK);
        buttonOK.setEnabled(false);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        contentPane.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }
        });


        buttonCancel.addActionListener(new ActionListener() {
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void updateButtonState() {
        String password = new String(passwordField.getPassword());
        buttonOK.setEnabled(!password.isEmpty());
    }

    private void onCancel() {
        // add your code here if necessary
        userCancelled = true;
        dispose();
    }

    public char[] getEnteredPassword() {
        return passwordField.getPassword();
    }

    public boolean didUserCancel() {
        return userCancelled;
    }

    public void resetField() {
        passwordField.setText("");
    }
}
