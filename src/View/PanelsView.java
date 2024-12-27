package View;

import javax.swing.*;

public class PanelsView extends JFrame {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private EmployeView employeView;
    private HolidayView holidayView;

    public PanelsView(HolidayView holidayView, EmployeView employeView) {
        this.employeView = employeView;
        this.holidayView = holidayView;

        setTitle("Admin Dashboard - Gestion des Congés et des Employés");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 520); // Adjusted size for the new panels
        setLocationRelativeTo(null);

        // Add the views as tabs to the JTabbedPane
        tabbedPane.addTab("Gestion des Employés", employeView);
        tabbedPane.addTab("Gestion des Congés", holidayView);

        // Add the JTabbedPane to the main window
        add(tabbedPane);

        // Ensure the window is visible
        setVisible(true);
        pack();
    }

    public EmployeView getEmployeView() {
        return employeView;
    }

    public HolidayView getHolidayView() {
        return holidayView;
    }
}
