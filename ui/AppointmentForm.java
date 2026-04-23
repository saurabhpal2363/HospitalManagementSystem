package ui;

import src.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AppointmentForm extends JFrame {
    private JComboBox<Patient> patientBox;
    private JComboBox<Doctor> doctorBox;
    private JTextField dateField, timeField, notesField;
    private JComboBox<String> statusBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private AppointmentDAO apptDao = new AppointmentDAO();
    private PatientDAO patientDao = new PatientDAO();
    private DoctorDAO doctorDao = new DoctorDAO();
    private int selectedId = -1;

    public AppointmentForm() {
        setTitle("Appointment Booking");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 245, 245));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Appointment"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        patientBox  = new JComboBox<>();
        doctorBox   = new JComboBox<>();
        dateField   = new JTextField("YYYY-MM-DD", 18);
        timeField   = new JTextField("e.g. 10:30 AM", 18);
        notesField  = new JTextField(18);
        statusBox   = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});

        loadPatients();
        loadDoctors();

        addFieldCombo(formPanel, g, "Patient:",          patientBox, 0);
        addFieldCombo(formPanel, g, "Doctor:",           doctorBox,  1);
        addField(formPanel, g,      "Date (YYYY-MM-DD):", dateField, 2);
        addField(formPanel, g,      "Time:",              timeField, 3);
        addField(formPanel, g,      "Notes:",             notesField,4);
        addFieldCombo(formPanel, g, "Status:",           statusBox,  5);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        JButton bookBtn   = createButton("Book",   new Color(231, 76, 60));
        JButton updateBtn = createButton("Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("Delete", new Color(149, 165, 166));
        JButton clearBtn  = createButton("Clear",  new Color(200, 200, 200));
        btnPanel.add(bookBtn); btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn); btnPanel.add(clearBtn);

        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(255, 182, 193));

        // Color-code status rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                String status = tableModel.getValueAt(row, 5).toString();
                if (!sel) {
                    c.setBackground(switch (status) {
                        case "Completed"  -> new Color(220, 255, 220);
                        case "Cancelled"  -> new Color(255, 220, 220);
                        default           -> Color.WHITE;
                    });
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("All Appointments"));

        add(formPanel, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        bookBtn.addActionListener(e -> {
            if (!validateForm()) return;
            Appointment a = getFormData();
            if (apptDao.bookAppointment(a)) {
                showMsg("Appointment booked!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(); clearForm();
            }
        });

        updateBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select an appointment first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            apptDao.updateStatus(selectedId, statusBox.getSelectedItem().toString());
            showMsg("Status updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(); clearForm();
        });

        deleteBtn.addActionListener(e -> {
            if (selectedId == -1) { showMsg("Select an appointment first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete appointment?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (apptDao.deleteAppointment(selectedId)) { refreshTable(); clearForm(); }
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                dateField.setText(tableModel.getValueAt(row, 3).toString());
                timeField.setText(tableModel.getValueAt(row, 4).toString());
                statusBox.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                notesField.setText(tableModel.getValueAt(row, 6).toString());
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

    private void addFieldCombo(JPanel p, GridBagConstraints g, String label, JComboBox<?> box, int row) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(box, g);
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

    private void loadPatients() {
        patientBox.removeAllItems();
        for (Patient p : patientDao.getAllPatients()) patientBox.addItem(p);
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
        for (Doctor d : doctorDao.getAllDoctors()) doctorBox.addItem(d);
    }

    private Appointment getFormData() {
        Appointment a = new Appointment();
        Patient p = (Patient) patientBox.getSelectedItem();
        Doctor d  = (Doctor)  doctorBox.getSelectedItem();
        if (p != null) a.setPatientId(p.getId());
        if (d != null) a.setDoctorId(d.getId());
        a.setAppointmentDate(dateField.getText().trim());
        a.setAppointmentTime(timeField.getText().trim());
        a.setStatus(statusBox.getSelectedItem().toString());
        a.setNotes(notesField.getText().trim());
        return a;
    }

    private boolean validateForm() {
        if (patientBox.getItemCount() == 0) { showMsg("No patients found. Add a patient first.", "Warning", JOptionPane.WARNING_MESSAGE); return false; }
        if (doctorBox.getItemCount() == 0)  { showMsg("No doctors found. Add a doctor first.", "Warning", JOptionPane.WARNING_MESSAGE); return false; }
        if (dateField.getText().trim().isEmpty() || dateField.getText().equals("YYYY-MM-DD")) {
            showMsg("Enter appointment date.", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        return true;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Appointment a : apptDao.getAllAppointments())
            tableModel.addRow(new Object[]{a.getId(), a.getPatientName(), a.getDoctorName(),
                a.getAppointmentDate(), a.getAppointmentTime(), a.getStatus(), a.getNotes()});
    }

    private void clearForm() {
        dateField.setText("YYYY-MM-DD");
        timeField.setText("e.g. 10:30 AM");
        notesField.setText("");
        statusBox.setSelectedIndex(0);
        selectedId = -1;
        table.clearSelection();
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}