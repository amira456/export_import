package Controller;

import Model.*;
import View.EmployeView;
import DAO.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;

public class EmployeController {
    private EmployeView view;
    private EmployeModel model;

    public EmployeController(EmployeView view, EmployeModel model) {
        this.view = view;
        this.model = model;
        this.view.setVisible(true);
        // Associer les boutons à leurs actions
        this.view.getAjouterButton().addActionListener(e -> add());
        this.view.getModifierButton().addActionListener(e -> update());
        this.view.getSupprimerButton().addActionListener(e -> delete());
        this.view.getAfficherButton().addActionListener(e -> employes());
        this.view.getExporter().addActionListener(e ->handleExport());
        this.view.getImporter().addActionListener(e -> handleImport());

        // Listener pour remplir les champs lors de la sélection d'une ligne dans la table
        this.view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Éviter les événements multiples lors de la sélection
                int selectedRow = view.table.getSelectedRow();
                if (selectedRow != -1) {
                    // Remplir les champs avec les données de la ligne sélectionnée
                    view.setNom(view.table.getValueAt(selectedRow, 1).toString());
                    view.setPrenom(view.table.getValueAt(selectedRow, 2).toString());
                    view.setEmail(view.table.getValueAt(selectedRow, 3).toString());
                    view.setTelephone(view.table.getValueAt(selectedRow, 4).toString());
                    view.setSalaire(Double.parseDouble(view.table.getValueAt(selectedRow, 5).toString()));
                    view.setRole((Role) view.table.getValueAt(selectedRow, 6)); // Assurez-vous que Role est castable
                    view.setPoste((Poste) view.table.getValueAt(selectedRow, 7)); // Assurez-vous que Poste est castable
                }
            }
        });
    }

    // Méthode pour ajouter un employé
    private void add() {
        String nom = view.getNom().trim();
        String prenom = view.getPrenom().trim();
        String email = view.getEmail().trim();
        String telephone = view.getTelephone().trim();
        Role role = view.getRole();
        Poste poste = view.getPoste();
        int balance = 0;

        Double salaire;
        try {
            salaire = view.getSalaire();
        } catch (NumberFormatException e) {
            view.afficherMessageErreur("Le salaire doit être un nombre valide.");
            return;
        }

        // Appel au modèle pour ajouter l'employé
        boolean ajoutReussi = model.add(nom, prenom, email, telephone, salaire, role, poste, balance);
        if (ajoutReussi) {
            view.afficherMessageSucces("Employé ajouté avec succès.");
            employes(); // Rafraîchir la table
        } else {
            view.afficherMessageErreur("Échec de l'ajout de l'employé.");
        }
    }

    // Méthode pour mettre à jour un employé
    private void update() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow == -1) {
            view.afficherMessageErreur("Veuillez sélectionner un employé à modifier.");
            return;
        }

        String nom = view.getNom().trim();
        String prenom = view.getPrenom().trim();
        String email = view.getEmail().trim();
        String telephone = view.getTelephone().trim();
        Role role = view.getRole();
        Poste poste = view.getPoste();
        int balance = 0;

        int id;
        try {
            id = (int) view.table.getValueAt(selectedRow, 0); // Corrected to use table model properly
        } catch (Exception e) {
            view.afficherMessageErreur("Erreur lors de la récupération de l'ID de l'employé.");
            return;
        }

        Double salaire;
        try {
            salaire = view.getSalaire();
        } catch (NumberFormatException e) {
            view.afficherMessageErreur("Le salaire doit être un nombre valide.");
            return;
        }

        boolean miseAJourReussie = model.update(id, nom, prenom, email, telephone, salaire, role, poste, balance);
        if (miseAJourReussie) {
            view.afficherMessageSucces("Employé modifié avec succès.");
            employes(); // Rafraîchir la table
        } else {
            view.afficherMessageErreur("Échec de la modification de l'employé.");
        }
    }

    // Méthode pour supprimer un employé
    private void delete() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow == -1) {
            view.afficherMessageErreur("Veuillez sélectionner un employé à supprimer.");
            return;
        }

        int id;
        try {
            id = (int) view.table.getValueAt(selectedRow, 0); // Corrected to use table model properly
        } catch (Exception e) {
            view.afficherMessageErreur("Erreur lors de la récupération de l'ID de l'employé.");
            return;
        }

        boolean suppressionReussie = model.delete(id);
        if (suppressionReussie) {
            view.afficherMessageSucces("Employé supprimé avec succès.");
            employes(); // Rafraîchir la table
        } else {
            view.afficherMessageErreur("Échec de la suppression de l'employé.");
        }
    }

    // Méthode pour afficher tous les employés
    private List<Employe> employes() {
        Object[][] employes = model.employes();
        view.model.setRowCount(0); // Vider la table avant d'ajouter les données
        for (Object[] emp : employes) {
            view.model.addRow(emp);
        }
        return null;
    }
    public void handleImport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv", "txt"));

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            model.importData(filePath); // Remplacement de employeModel par model
            view.afficherMessageSucces("Importation réussie !");
        }
    }

    public void handleExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                List<Employe> employes =new EmployeDAOImp().getAll();
                model.exportData(filePath, employes);
                view.afficherMessageSucces("Exportation réussie !");
            } catch (Exception ex) {
                view.afficherMessageErreur("Une erreur inattendue est survenue : " + ex.getMessage());
            }
        }
    }
}
