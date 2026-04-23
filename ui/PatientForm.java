package ui;

import src.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PatientForm extends JFrame {
    private JTextField nameField, ageField, phoneField, addressField;
    private JComboBox<String> genderBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private PatientDAO dao = new PatientDAO();
    private int selectedId = -1;

    public PatientForm() {
        setTitle("Patient Registration");
        setSize(860, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 245, 255));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        nameField    = new JTextField(18);
        ageField     = new JTextField(18);
        phoneField   = new JTextField(18);
        addressField = new JTextField(18);
        genderBox    = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        addField(formPanel, g, "Name:",    nameField,    0);
        addField(formPanel, g, "Age:",     ageField,     1);
        addField(formPanel, g, "Gender:",  genderBox,    2);
        addField(formPanel, g, "Phone:",   phoneField,   3);
        addField(formPanel, g, "Address:", addressField, 4);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        JButton addBtn    = createButton("Add",    new Color(46, 204, 113));
        JButton updateBtn = createButton("Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("Delete", new Color(231, 76, 60));
        JButton clearBtn  = createButton("Clear",  new Color(149, 165, 166));
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        // Table
        String[] cols = {"ID", "Name", "Age", "Gender", "Phone", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(173, 216, 230));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Registered Patients"));

        add(formPanel, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        // Button actions
        addBtn.addActionListener(e -> {
            if (validateForm()) {
                Patient p = getFormData();
                if (dao.addPatient(p)) {
                    showMsg("Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    clearForm();
                } else {
                    showMsg("Failed to add patient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select a patient to update.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            if (validateForm()) {
                Patient p = getFormData();
                p.setId(selectedId);
                if (dao.updatePatient(p)) {
                    showMsg("Patient updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    clearForm();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select a patient to delete.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this patient?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deletePatient(selectedId)) {
                    showMsg("Patient deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    clearForm();
                }
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                ageField.setText(tableModel.getValueAt(row, 2).toString());
                genderBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                phoneField.setText(tableModel.getValueAt(row, 4).toString());
                addressField.setText(tableModel.getValueAt(row, 5).toString());
            }
        });

        refreshTable();
        setVisible(true);
    }

    private void addField(JPanel p, GridBagConstraints g, String label, JComponent field, int row) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(field, g);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(90, 35));
        return btn;
    }

    private Patient getFormData() {
        return new Patient(
            nameField.getText().trim(),
            Integer.parseInt(ageField.getText().trim()),
            genderBox.getSelectedItem().toString(),
            phoneField.getText().trim(),
            addressField.getText().trim()
        );
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) { showMsg("Name is required.", "Validation", JOptionPane.WARNING_MESSAGE); return false; }
        try { Integer.parseInt(ageField.getText().trim()); } catch (Exception e) { showMsg("Valid age required.", "Validation", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Patient> patients = dao.getAllPatients();
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()});
        }
    }

    private void clearForm() {
        nameField.setText(""); ageField.setText(""); phoneField.setText(""); addressField.setText("");
        genderBox.setSelectedIndex(0);
        selectedId = -1;
        table.clearSelection();
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}