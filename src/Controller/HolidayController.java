package Controller;

import Model.*;
import View.HolidayView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HolidayController {
    private HolidayView view;
    private HolidayModel model;

    public HolidayController(HolidayView view, HolidayModel model) {
        this.view = view;
        this.model = model;
        this.view .setVisible(true);
        // Récupérer les employés et remplir la combobox
        List<String> employes = model.getEmployes();
        view.setEmployes(employes);

        // Ajouter les écouteurs pour les boutons
        this.view.getAjouterButton().addActionListener(e -> addHoliday());
        this.view.getSupprimerButton().addActionListener(e -> deleteHoliday());
        this.view.getModifierButton().addActionListener(e ->updateHoliday());
        this.view.getAfficherButton().addActionListener(e -> showHolidays());

        // Ajouter un écouteur pour remplir les champs lors de la sélection dans la table
        this.view.getTable().getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow != -1) {
                view.setEmploye(view.getTable().getValueAt(selectedRow, 1).toString());
                view.getTypeComboBox().setSelectedItem(Type.valueOf(view.getTable().getValueAt(selectedRow, 2).toString()));
                view.getStartDateField().setText(view.getTable().getValueAt(selectedRow, 3).toString());
                view.getEndDateField().setText(view.getTable().getValueAt(selectedRow, 4).toString());
            }
        });
    }

    // Ajouter un congé
    private void addHoliday() {
        try {
            if (validateInputs()) {
                String employe = view.getEmploye().trim();
                Type type = view.getType();
                String startDateFormatted = formatDate(view.getStartDate().trim());
                String endDateFormatted = formatDate(view.getEndDate().trim());

                boolean ajoutReussi = model.add(employe, type, startDateFormatted, endDateFormatted);
                if (ajoutReussi) {
                    view.afficherMessageSucces("Congé ajouté avec succès.");
                    showHolidays();
                } else {
                    view.afficherMessageErreur("Échec de l'ajout du congé.");
                }
            }
        } catch (Exception e) {
            view.afficherMessageErreur("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }
    // Mettre à jour un congé
    private void updateHoliday() {
        try {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow == -1) {
                view.afficherMessageErreur("Veuillez sélectionner un congé à mettre à jour.");
                return;
            }

            if (validateInputs()) {
                int id = (int) view.getTable().getValueAt(selectedRow, 0);
                String employe = view.getEmploye().trim();
                Type type = view.getType();
                String startDateFormatted = formatDate(view.getStartDate().trim());
                String endDateFormatted = formatDate(view.getEndDate().trim());

                boolean miseAJourReussie = model.update(id, employe, type, startDateFormatted, endDateFormatted);
                if (miseAJourReussie) {
                    view.afficherMessageSucces("Congé mis à jour avec succès.");
                    showHolidays();
                } else {
                    view.afficherMessageErreur("Échec de la mise à jour du congé.");
                }
            }
        } catch (Exception e) {
            view.afficherMessageErreur("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }
    // Supprimer un congé
    private void deleteHoliday() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1) {
            view.afficherMessageErreur("Veuillez sélectionner un congé à supprimer.");
            return;
        }
        int id = (int) view.getTable().getValueAt(selectedRow, 0);

        boolean suppressionReussie = model.delete(id);
        if (suppressionReussie) {
            view.afficherMessageSucces("Congé supprimé avec succès.");
            showHolidays();
        } else {
            view.afficherMessageErreur("Échec de la suppression du congé.");
        }
    }

    // Afficher tous les congés
    private void showHolidays() {
        Object[][] holidays = model.getAllHolidaysAsTableData();
        view.getModel().setRowCount(0); // Vider la table avant d'ajouter les nouvelles données
        for (Object[] holiday : holidays) {
            view.getModel().addRow(holiday);
        }
    }

    // Valider les entrées des utilisateurs
    private boolean validateInputs() {
        String employe = view.getEmploye().trim();
        Type type = view.getType();
        String startDate = view.getStartDate().trim();
        String endDate = view.getEndDate().trim();

        if (employe.isEmpty() || type == null || startDate.isEmpty() || endDate.isEmpty()) {
            view.afficherMessageErreur("Tous les champs doivent être remplis.");
            return false;
        }

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            view.afficherMessageErreur("Les dates doivent être au format AAAA-MM-JJ.");
            return false;
        }

        return true;
    }

    // Vérifier si une date est valide
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Formater la date au format attendu
    private String formatDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(sdf.parse(date));
        } catch (ParseException e) {
            view.afficherMessageErreur("Format de date invalide.");
            return null;
        }
    }
}
