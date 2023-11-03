package dev.pyojan.gui;

import dev.pyojan.Main;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class CertificateListChoose extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable listOfCertificateTable;
    @Getter
    private boolean isUserDidCancel = false;
    @Getter
    private String selectedCertificate = null;

    private List<Object[]> certificateList = new ArrayList<>();

    public CertificateListChoose() {
        init();
    }

    public static void main(String[] args) {
        CertificateListChoose dialog = new CertificateListChoose();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    // New method to update the table data with a new certificate list
    public void setCertificateList(List<Object[]> certificateList) {
        this.certificateList = certificateList;
        updateTableData();

        setVisible(true);
    }

    private void updateTableData() {
        DefaultTableModel defaultTableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Name", "S.N", "Expiry"}
        );

        // Add data rows
        if (!certificateList.isEmpty()) {
            certificateList.forEach(cert -> {
                defaultTableModel.addRow(new Object[]{cert[0], cert[1], cert[2]});
            });
        }

        listOfCertificateTable.setModel(defaultTableModel);
        validate();
    }

    private void init() {

        setContentPane(contentPane);
        setModal(true);
        setTitle("Select your certificate");
        getRootPane().setDefaultButton(buttonOK);


        ClassLoader classLoader = Main.class.getClassLoader();
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(classLoader.getResource("logo.png")));
        setIconImage(imageIcon.getImage());


        // Window side [Input box size is 300]
        int width = 500;
        int height = 300;
        setPreferredSize(new Dimension(width, height));

        // Calculate screen center
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        int x = (int) ((bounds.getWidth() - width) / 2);
        int y = (int) ((bounds.getHeight() - height) / 2);
        setLocation(x, y);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        pack();
        validate();
    }

    public Object[] getRawValue() {
        int selectedRow = listOfCertificateTable.getSelectedRow();
        if (selectedRow != -1) { // Check if any row is selected
            DefaultTableModel model = (DefaultTableModel) listOfCertificateTable.getModel();
            Vector<?> rowData = (Vector<?>) model.getDataVector().elementAt(selectedRow);
            return rowData.toArray(new Object[0]);
        }
        return null; // Return null if no row is selected
    }

    private void onOK() {
        Object[] selectedRawValues = getRawValue();
        if (selectedRawValues != null) {
            selectedCertificate = (String) selectedRawValues[1];
            dispose(); // Dispose the dialog if there are selected values
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        isUserDidCancel = true;
        dispose();
    }
}
