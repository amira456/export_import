package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import Model.Type;

public class HolidayView extends JPanel {
    // Déclaration des panels
    private JPanel pan1 = new JPanel();
    private JPanel pan2 = new JPanel();
    private JPanel pan4 = new JPanel();

    // Boutons
    private JButton ajouter = new JButton("Ajouter ");
    private JButton modifier = new JButton("Modifier "); // Nouveau bouton pour modifier
    private JButton supprimer = new JButton("Supprimer ");
    private JButton afficher = new JButton("Afficher");

    // Labels
    private JLabel employeLabel = new JLabel("Employé:");
    private JLabel typeLabel = new JLabel("Type de Congé:");
    private JLabel startDateLabel = new JLabel("Date de Début:");
    private JLabel endDateLabel = new JLabel("Date de Fin:");

    // Champs de texte
    private JTextField startDateField = new JTextField(20);
    private JTextField endDateField = new JTextField(20);

    // Combobox pour les employés et le type de congé
    private JComboBox<String> employeComboBox = new JComboBox<>();
    private JComboBox<Type> typeComboBox = new JComboBox<>(Type.values());

    // Table pour afficher les congés
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public HolidayView() {
        // Configuration du panel principal
        pan1.setLayout(new BorderLayout());
        pan1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajout d'un espace intérieur

        // Configuration du panel contenant les champs
        pan2.setLayout(new GridLayout(5, 2, 2, 2)); // Réduction de l'espacement horizontal et vertical
        pan2.add(employeLabel);
        pan2.add(employeComboBox);
        pan2.add(typeLabel);
        pan2.add(typeComboBox);
        pan2.add(startDateLabel);
        pan2.add(startDateField);
        pan2.add(endDateLabel);
        pan2.add(endDateField);

        // Configuration du panel pour les boutons
        pan4.setLayout(new GridLayout(1, 4, 8, 8)); // Ajustement de l'espacement pour 4 boutons
        pan4.add(ajouter);
        pan4.add(modifier);
        pan4.add(supprimer);
        pan4.add(afficher);

        // Table pour afficher les congés
        String[] columnNames = {"Id", "Employé", "Type de Congé", "Date de Début", "Date de Fin"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);

        // Ajout des panneaux au panel principal
        pan1.add(pan2, BorderLayout.NORTH);
        pan1.add(scrollPane, BorderLayout.CENTER);
        pan1.add(pan4, BorderLayout.SOUTH);

        // Ajout du panel principal à la vue
        add(pan1);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    // Getters et setters pour les boutons
    public JButton getAjouterButton() {
        return ajouter;
    }

    public JButton getModifierButton() {
        return modifier;
    }

    public JButton getSupprimerButton() {
        return supprimer;
    }

    public JButton getAfficherButton() {
        return afficher;
    }

    // Getters et setters pour les champs de texte
    public JTextField getStartDateField() {
        return startDateField;
    }

    public JTextField getEndDateField() {
        return endDateField;
    }

    // Getters et setters pour les ComboBox
    public JComboBox<String> getEmployeComboBox() {
        return employeComboBox;
    }

    public JComboBox<Type> getTypeComboBox() {
        return typeComboBox;
    }

    // Getters pour la table et son modèle
    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    // Méthodes pour afficher les messages d'erreur et de succès
    public void afficherMessageErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void afficherMessageSucces(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    // Méthode pour définir les employés dans le ComboBox
    public void setEmployes(List<String> employes) {
        employeComboBox.setModel(new DefaultComboBoxModel<>(employes.toArray(new String[0])));
    }

    // Méthode pour récupérer l'employé sélectionné
    public String getEmploye() {
        return (String) employeComboBox.getSelectedItem();
    }

    // Méthode pour définir l'employé sélectionné
    public void setEmploye(String employe) {
        employeComboBox.setSelectedItem(employe);
    }

    // Méthode pour récupérer le type de congé sélectionné
    public Type getType() {
        return (Type) typeComboBox.getSelectedItem();
    }

    // Méthode pour définir le type de congé sélectionné
    public void setType(Type type) {
        typeComboBox.setSelectedItem(type);
    }

    // Méthodes pour récupérer et définir les dates
    public String getStartDate() {
        return startDateField.getText();
    }

    public void setStartDate(String startDate) {
        startDateField.setText(startDate);
    }

    public String getEndDate() {
        return endDateField.getText();
    }

    public void setEndDate(String endDate) {
        endDateField.setText(endDate);
    }
}
