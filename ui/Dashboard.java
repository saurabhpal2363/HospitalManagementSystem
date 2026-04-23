package ui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("🏥 Hospital Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 80, 160));
        header.setPreferredSize(new Dimension(700, 70));
        JLabel title = new JLabel("Hospital Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.setLayout(new BorderLayout());
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Center: Navigation buttons
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(240, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Cards
        String[][] cards = {
            {"👤 Patient Registration", "Register, view, update & delete patients"},
            {"🩺 Doctor Scheduling", "Manage doctors and their schedules"},
            {"📅 Appointment Booking", "Book and manage appointments"}
        };
        Color[] colors = {
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(231, 76, 60)
        };

        for (int i = 0; i < cards.length; i++) {
            final int index = i;
            JPanel card = createCard(cards[i][0], cards[i][1], colors[i]);
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    openModule(index);
                }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(colors[index].darker());
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(colors[index]);
                }
            });
            gbc.gridx = i;
            gbc.gridy = 0;
            center.add(card, gbc);
        }
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(30, 80, 160));
        footer.setPreferredSize(new Dimension(700, 35));
        JLabel footerLabel = new JLabel("© 2025 Hospital Management System — Mini Version");
        footerLabel.setForeground(new Color(180, 200, 240));
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(footerLabel);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createCard(String title, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(180, 160));

        JLabel t = new JLabel("<html><center>" + title + "</center></html>", SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(Color.WHITE);

        JLabel s = new JLabel("<html><center>" + subtitle + "</center></html>", SwingConstants.CENTER);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(new Color(220, 235, 255));

        card.add(t, BorderLayout.CENTER);
        card.add(s, BorderLayout.SOUTH);
        return card;
    }

    private void openModule(int index) {
        switch (index) {
            case 0 -> new PatientForm();
            case 1 -> new DoctorForm();
            case 2 -> new AppointmentForm();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Dashboard::new);
    }
}