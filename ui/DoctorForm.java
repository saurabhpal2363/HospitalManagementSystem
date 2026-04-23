package ui;

import src.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DoctorForm extends JFrame {
    private JTextField nameField, specField, phoneField, daysField, timeField;
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO dao = new DoctorDAO();
    private int selectedId = -1;

    public DoctorForm() {
        setTitle("Doctor Scheduling");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 255, 245));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Doctor Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        nameField  = new JTextField(18);
        specField  = new JTextField(18);
        phoneField = new JTextField(18);
        daysField  = new JTextField(18);   // e.g. Mon, Tue, Wed
        timeField  = new JTextField(18);   // e.g. 09:00–17:00

        addField(formPanel, g, "Name:",           nameField,  0);
        addField(formPanel, g, "Specialization:", specField,  1);
        addField(formPanel, g, "Phone:",          phoneField, 2);
        addField(formPanel, g, "Available Days:", daysField,  3);
        addField(formPanel, g, "Available Time:", timeField,  4);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        JButton addBtn    = createButton("Add",    new Color(46, 204, 113));
        JButton updateBtn = createButton("Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("Delete", new Color(231, 76, 60));
        JButton clearBtn  = createButton("Clear",  new Color(149, 165, 166));
        btnPanel.add(addBtn); btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn); btnPanel.add(clearBtn);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        String[] cols = {"ID", "Name", "Specialization", "Phone", "Available Days", "Available Time"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(144, 238, 144));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Registered Doctors"));

        add(formPanel, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            if (validateForm()) {
                Doctor d = getFormData();
                if (dao.addDoctor(d)) {
                    showMsg("Doctor added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(); clearForm();
                }
            }
        });

        updateBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select a doctor first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            Doctor d = getFormData(); d.setId(selectedId);
            if (dao.updateDoctor(d)) {
                showMsg("Doctor updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(); clearForm();
            }
        });

        deleteBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select a doctor first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete doctor?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dao.deleteDoctor(selectedId)) { refreshTable(); clearForm(); }
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                specField.setText(tableModel.getValueAt(row, 2).toString());
                phoneField.setText(tableModel.getValueAt(row, 3).toString());
                daysField.setText(tableModel.getValueAt(row, 4).toString());
                timeField.setText(tableModel.getValueAt(row, 5).toString());
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

    private Doctor getFormData() {
        return new Doctor(
            nameField.getText().trim(), specField.getText().trim(),
            phoneField.getText().trim(), daysField.getText().trim(), timeField.getText().trim()
        );
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) { showMsg("Name is required.", "Validation", JOptionPane.WARNING_MESSAGE); return false; }
        if (specField.getText().trim().isEmpty()) { showMsg("Specialization required.", "Validation", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Doctor d : dao.getAllDoctors())
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpecialization(), d.getPhone(), d.getAvailableDays(), d.getAvailableTime()});
    }

    private void clearForm() {
        nameField.setText(""); specField.setText(""); phoneField.setText("");
        daysField.setText(""); timeField.setText("");
        selectedId = -1; table.clearSelection();
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}